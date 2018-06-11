package org.snail.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.snail.common.exception.remoting.RemotingSendRequestException;
import org.snail.common.exception.remoting.RemotingTimeoutException;
import org.snail.common.protocal.SnailProtocol;
import org.snail.common.utils.NativeSupport;
import org.snail.remoting.ConnectionUtils;
import org.snail.remoting.RPCHook;
import org.snail.remoting.model.NettyChannelInactiveProcessor;
import org.snail.remoting.model.NettyRequestProcessor;
import org.snail.remoting.model.RemotingResponse;
import org.snail.remoting.model.RemotingTransporter;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maxlcat on 2018/6/6.
 */
@Slf4j
public abstract class NettyRemotingBase {

    protected final ConcurrentHashMap<Long, RemotingResponse> responseMap = new ConcurrentHashMap<>();

    protected Pair<NettyRequestProcessor, ExecutorService> defaultRequestProcessor;
  
    protected Pair<NettyChannelInactiveProcessor, ExecutorService> defaultChannelInactiveProcessor;

    protected final ExecutorService publicExecutor = Executors.newFixedThreadPool(4, new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "NettyClientPublicExecutor_" + threadIndex.incrementAndGet());
        }
    });

    protected final HashMap<Byte, Pair<NettyRequestProcessor, ExecutorService>> processorMap = new HashMap<>();

    protected abstract RPCHook getRPCHook();


    public RemotingTransporter invokeSyncImpl(final Channel channel, final RemotingTransporter request, final long timeoutMills)
        throws RemotingTimeoutException, RemotingSendRequestException, InterruptedException {
        try {
            final RemotingResponse remotingResponse = new RemotingResponse(request.getOpaque(), null, timeoutMills);
            responseMap.put(request.getOpaque(), remotingResponse);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) (future) -> {
                if (future.isSuccess()) {
                    remotingResponse.setSendRequestOk(true);
                    return;
                }
                remotingResponse.setSendRequestOk(false);
                responseMap.remove(request.getOpaque());
                remotingResponse.setCause(future.cause());
                remotingResponse.putResponse(null);
                log.warn("use channel [{}] send msg [{}] failed and reason is [{}]", channel, request, future.cause().getMessage());
            });
            RemotingTransporter remotingTransporter = remotingResponse.waitResponse();
            if (remotingTransporter == null) {
                if (remotingResponse.isSendRequestOk()) {
                    throw new RemotingTimeoutException(ConnectionUtils.parseChannelRemoeAddress(channel), timeoutMills, remotingResponse.getCause());
                } else {
                    throw new RemotingSendRequestException(ConnectionUtils.parseChannelRemoeAddress(channel), remotingResponse.getCause());
                }
            }
            return remotingTransporter;
        } finally {
            responseMap.remove(request.getOpaque());
        }
    }

    protected void processMessageReceived(ChannelHandlerContext ctx, RemotingTransporter msg) {
        if (log.isDebugEnabled()) {
            log.debug("channel [{}] received RemotingTransporter is {}", ctx.channel(), msg);
        }
        final RemotingTransporter remotingTransporter = msg;
        if (remotingTransporter != null) {
            switch (remotingTransporter.getTransporterType()) {
                case SnailProtocol.REQUEST_REMOTING:
                    processRemotingRequest(ctx, remotingTransporter);
                    break;
                case SnailProtocol.RESPONSE_REMOTING:
                    processRemotingResponse(ctx, msg);
                    break;
                default:
                    break;
            }
        }
    }

    protected void processChannelInactive(final ChannelHandlerContext ctx) {
        final Pair<NettyChannelInactiveProcessor, ExecutorService> pair = this.defaultChannelInactiveProcessor;
        if (pair != null) {
            Runnable runnable = () -> {
                try {
                    pair.getKey().processChannelInactive(ctx);
                } catch (InterruptedException e) {
                    log.error("server exception [{}]", e.getMessage());
                }
            };
            try {
                pair.getValue().submit(runnable);
            } catch (Exception e) {
                log.error("server is busy [{}]", e.getMessage());
            }

        }
    }

    protected void processRemotingRequest(final ChannelHandlerContext ctx, final RemotingTransporter remotingTransporter) {
        final Pair<NettyRequestProcessor, ExecutorService> tempPair = this.processorMap.get(remotingTransporter.getCode());
        final Pair<NettyRequestProcessor, ExecutorService> pair = tempPair == null ? this.defaultRequestProcessor : tempPair;
        if (pair != null) {
            Runnable run = () -> {
                try {
                    RPCHook rpcHook = NettyRemotingBase.this.getRPCHook();
                    String remotingAddress = ConnectionUtils.parseChannelRemoeAddress(ctx.channel());
                    if (rpcHook != null) {
                        rpcHook.doBeforeRequest(remotingAddress, remotingTransporter);
                    }
                    final RemotingTransporter response = pair.getKey().processRequest(ctx, remotingTransporter);
                    if (rpcHook != null) {
                        rpcHook.doAfterResponse(remotingAddress, remotingTransporter, response);
                    }
                    if (response != null) {
                        ctx.writeAndFlush(response).addListener((ChannelFutureListener)future -> {
                            if (!future.isSuccess()) {
                                log.error("fail send response exception is [{}]", future.cause().getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("processor occur exception [{}]", e.getMessage());
                    final RemotingTransporter response = RemotingTransporter.newInstance(remotingTransporter.getOpaque(),
                        SnailProtocol.RESPONSE_REMOTING, SnailProtocol.HANDLER_ERROR, null);
                    ctx.writeAndFlush(response);
                }
            };
            try {
                pair.getValue().submit(run);
            } catch (Exception e) {
                log.error("server is busy {}", e.getMessage());
                final RemotingTransporter response = RemotingTransporter.newInstance(remotingTransporter.getOpaque(),
                    SnailProtocol.RESPONSE_REMOTING, SnailProtocol.HANDLER_BUSY, null);
                ctx.writeAndFlush(response);
            }
        }
    }

    protected void processRemotingResponse(ChannelHandlerContext ctx, RemotingTransporter remotingTransporter) {
        final RemotingResponse remotingResponse = responseMap.get(remotingTransporter.getOpaque());
        if (remotingResponse != null) {
            remotingResponse.setTransporter(remotingTransporter);
            remotingResponse.putResponse(remotingTransporter);
            responseMap.remove(remotingTransporter.getOpaque());
        } else {
            log.warn("received response bu matched Id is removed from responseMap maybe timeout");
        }
    }

    protected EventLoopGroup initEventLoopGroup(int works, ThreadFactory bossFactory) {
        return isNativeET() ? new EpollEventLoopGroup(works, bossFactory) : new NioEventLoopGroup(works, bossFactory);
    }

    protected boolean isNativeET() {
        return NativeSupport.isSupportNativeET();
    }

    protected  <T> void setIoRatio(T o, int ratio) {
        if (o instanceof EpollEventLoopGroup) {
            ((EpollEventLoopGroup) o).setIoRatio(ratio);
        } else if (o instanceof NioEventLoopGroup) {
            ((NioEventLoopGroup) o).setIoRatio(ratio);
        }
    }



}

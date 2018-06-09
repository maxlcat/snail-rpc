package org.snail.remoting.netty;

import io.netty.channel.Channel;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.snail.common.exception.remoting.RemotingSendRequestException;
import org.snail.common.exception.remoting.RemotingTimeoutException;
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


    public RemotingTransporter invokeSyncImpl(final Channel channel, final RemotingTransporter transporter, final long timeoutMills)
        throws RemotingTimeoutException, RemotingSendRequestException, InterruptedException {
        try {

        } finally {

        }
    }




}

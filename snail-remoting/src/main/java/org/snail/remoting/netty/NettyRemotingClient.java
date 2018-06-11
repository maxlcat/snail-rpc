package org.snail.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.snail.remoting.RPCHook;
import org.snail.remoting.model.NettyChannelInactiveProcessor;
import org.snail.remoting.model.NettyRequestProcessor;
import org.snail.remoting.model.RemotingTransporter;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
@Slf4j
public class NettyRemotingClient extends NettyRemotingBase implements RemotingClient{

  private EventLoopGroup worker;
  private Bootstrap bootstrap;
  private int nWorkers;
  protected volatile ByteBufAllocator allocator;
  private final Lock lockChannel = new ReentrantLock();
  private static final long LOCK_TIMEOUT_MILLIS = 3000;
  private DefaultEventExecutorGroup defaultEventExecutorGroup;
  private final NettyClientConfig nettyClientConfig;
  private volatile int writeBufferHighWaterMark = -1;
  private volatile int writeBufferLowWaterMark = -1;
  private RPCHook rpcHook;

  public NettyRemotingClient(NettyClientConfig nettyClientConfig) {
    this.nettyClientConfig = nettyClientConfig;
    if (nettyClientConfig != null) {
      nWorkers = nettyClientConfig.getClientWorkerThreads();
      writeBufferHighWaterMark = nettyClientConfig.getWriteBufferHighWaterMark();
      writeBufferLowWaterMark = nettyClientConfig.getWriteBufferLowWaterMark();
    }
    init();
  }

  @Override
  protected RPCHook getRPCHook() {
    return rpcHook;
  }

  @Override
  public RemotingTransporter invokeSync(String address, RemotingTransporter request,
      long timeoutMillis) throws InterruptedException {
    return null;
  }

  @Override
  public void registerProcessor(byte requestCode, NettyRequestProcessor processor,
      ExecutorService executor) {

  }

  @Override
  public void registerChannelInactiveProcessor(NettyChannelInactiveProcessor processor,
      ExecutorService executor) {

  }

  @Override
  public boolean isChannelWriteAble(String address) {
    return false;
  }

  @Override
  public void activeConnect(boolean isReconnect) {

  }

  @Override
  public void init() {
    ThreadFactory workerFactory = new DefaultThreadFactory("netty.client");
    worker = initEventLoopGroup(nWorkers, workerFactory);
    bootstrap = new Bootstrap().group(worker);
    setIoRatio(worker, 100);
    bootstrap.option(ChannelOption.ALLOCATOR, allocator).option(ChannelOption.MESSAGE_SIZE_ESTIMATOR,
        DefaultMessageSizeEstimator.DEFAULT).option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
        (int) TimeUnit.SECONDS.toMillis(3)).option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.ALLOW_HALF_CLOSURE, false);
    if (writeBufferLowWaterMark >= 0 && writeBufferHighWaterMark > 0) {
      WriteBufferWaterMark waterMark = new WriteBufferWaterMark(writeBufferLowWaterMark, writeBufferHighWaterMark);
      bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, waterMark);
    }
  }

  @Override
  public void start() {
    defaultEventExecutorGroup = new DefaultEventExecutorGroup(
        nettyClientConfig.getClientWorkerThreads(), new ThreadFactory() {
      private AtomicInteger threadIndex = new AtomicInteger(0);
      @Override
      public Thread newThread(Runnable r) {
        return new Thread(r, "NettyClientWorkerThread_" + threadIndex.incrementAndGet());
      }
    });
    if (isNativeET()) {
      bootstrap.channel(EpollSocketChannel.class);
    } else {
      bootstrap.channel(NioSocketChannel.class);
    }
  }

  @Override
  public void shutdown() {

  }

  @Override
  public void registerPRCHook(RPCHook rpcHook) {

  }
}

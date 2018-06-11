package org.snail.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.snail.common.exception.remoting.RemotingSendRequestException;
import org.snail.common.exception.remoting.RemotingTimeoutException;
import org.snail.common.utils.Constants;
import org.snail.common.utils.NameThreadFactory;
import org.snail.common.utils.NativeSupport;
import org.snail.remoting.RPCHook;
import org.snail.remoting.model.NettyChannelInactiveProcessor;
import org.snail.remoting.model.NettyRequestProcessor;
import org.snail.remoting.model.RemotingTransporter;
import org.snail.remoting.netty.decode.RemotingTransporterDecoder;
import org.snail.remoting.netty.encode.RemotingTransporterEncoder;
import org.snail.remoting.netty.idle.AcceptorIdleStateTrigger;
import org.snail.remoting.netty.idle.IdleStateChecker;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
@Slf4j
public class NettyRemotingServer extends NettyRemotingBase implements RemotingServer{

  private ServerBootstrap serverBootstrap;
  private EventLoopGroup boss;
  private EventLoopGroup worker;
  private int workerNum;

  private int writeBufferLowWaterMark;
  private int writeBufferHighWaterMark;
  protected final HashedWheelTimer timer = new HashedWheelTimer(new NameThreadFactory("netty.acceptor.timer"));

  protected volatile ByteBufAllocator allocator;
  private final NettyServerConfig nettyServerConfig;
  private DefaultEventExecutorGroup defaultEventExecutorGroup;
  private final ExecutorService publicExecutor;
  private final AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();
  private RPCHook rpcHook;

  public NettyRemotingServer() {
    this(new NettyServerConfig());
  }

  public NettyRemotingServer(NettyServerConfig nettyServerConfig) {
    this.nettyServerConfig = nettyServerConfig;
    if (nettyServerConfig != null) {
      workerNum = nettyServerConfig.getServerWorkerThreads();
      writeBufferHighWaterMark = nettyServerConfig.getWriteBufferHighWaterMark();
      writeBufferLowWaterMark = nettyServerConfig.getWriteBufferLowWaterMark();
    }
    this.publicExecutor = Executors.newFixedThreadPool(4, new ThreadFactory() {
      private AtomicInteger threadIndex = new AtomicInteger(0);
      @Override
      public Thread newThread(Runnable r) {
        return new Thread(r, "NettyServerPublicExecutor_" + this.threadIndex.incrementAndGet());
      }
    });
    init();
  }

  @Override
  public RemotingTransporter invokeSync(Channel channel, RemotingTransporter request,
      long timeoutMillis) throws RemotingSendRequestException, RemotingTimeoutException, InterruptedException{
    return super.invokeSyncImpl(channel, request, timeoutMillis);
  }

  @Override
  public void registerProcessor(byte requestCode, NettyRequestProcessor processor,
      ExecutorService executorService) {
    ExecutorService executor = executorService == null ? publicExecutor : executorService;
    Pair<NettyRequestProcessor, ExecutorService> pair = new Pair<>(processor, executor);
    this.processorMap.put(requestCode, pair);
  }

  @Override
  public void registerChannelInactiveProcessor(NettyChannelInactiveProcessor processor,
      ExecutorService executorService) {
    if (executorService == null) {
      executorService = publicExecutor;
    }
    this.defaultChannelInactiveProcessor = new Pair<NettyChannelInactiveProcessor,ExecutorService>(processor, executorService);
  }

  @Override
  public void registerDefaultProcessor(NettyRequestProcessor processor,
      ExecutorService executorService) {
    this.defaultRequestProcessor = new Pair<NettyRequestProcessor, ExecutorService>(processor, executorService);
  }

  @Override
  public void init() {
    ThreadFactory bossFactory = new DefaultThreadFactory("netty.boss");
    ThreadFactory workFactory = new DefaultThreadFactory("netty.worker");
    boss = initEventLoopGroup(1, bossFactory);
    if (workerNum <= 0) {
      workerNum = Runtime.getRuntime().availableProcessors() << 1;
    }
    worker = initEventLoopGroup(workerNum, workFactory);
    serverBootstrap = new ServerBootstrap().group(boss, worker);
    allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
    serverBootstrap.childOption(ChannelOption.ALLOCATOR, allocator)
        .childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
        .childOption(ChannelOption.SO_REUSEADDR,true)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
    setIoRatio(boss, 100);
    setIoRatio(worker, 100);
    serverBootstrap.option(ChannelOption.SO_BACKLOG, 32768);
    serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);

    if (writeBufferLowWaterMark >= 0 && writeBufferHighWaterMark > 0) {
      WriteBufferWaterMark waterMark = new WriteBufferWaterMark(writeBufferLowWaterMark, writeBufferHighWaterMark);
      serverBootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, waterMark);
    }
  }

  @Override
  public void start() {
    this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(Constants.AVAILABLE_PROCESSORS,
        new ThreadFactory() {
          private AtomicInteger threadIndex = new AtomicInteger(0);
          @Override
          public Thread newThread(Runnable r) {
            return new Thread(r, "NettyServerWorkerThread_" + this.threadIndex.incrementAndGet());
          }
        });
    if (isNativeET()) {
      serverBootstrap.channel(EpollServerSocketChannel.class);
    } else {
      serverBootstrap.channel(NioServerSocketChannel.class);
    }

    serverBootstrap.localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort())).childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(defaultEventExecutorGroup,
                new IdleStateChecker(timer, Constants.READER_IDLE_TIME_SECONDS, 0, 0),
                idleStateTrigger,
                new RemotingTransporterDecoder(),
                new RemotingTransporterEncoder(),
                new NettyServerHandler());
          }
        });

    try {
      log.info("netty bind [{}] serverBootstrap start...", this.nettyServerConfig.getListenPort());
      this.serverBootstrap.bind().sync();
      log.info("netty start success at port [{}]", this.nettyServerConfig.getListenPort());
    } catch (InterruptedException e) {
      log.error("start serverBootstrap exception [{}]", e.getMessage());
      throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e);
    }
  }

  @Override
  public void shutdown() {
    try {
      if (this.timer != null) {
        this.timer.stop();
      }
      this.boss.shutdownGracefully();
      this.worker.shutdownGracefully();
      if (this.defaultEventExecutorGroup != null) {
        this.defaultEventExecutorGroup.shutdownGracefully();
      }
    } catch (Exception e) {
      log.error("NettyRemotingServer shutdown exception {}", e);
    }
    if (this.publicExecutor != null) {
      this.publicExecutor.shutdown();
    }
  }

  @Override
  public void registerPRCHook(RPCHook rpcHook) {
    this.rpcHook = rpcHook;
  }




  @Override
  protected RPCHook getRPCHook() {
    return rpcHook;
  }

  class NettyServerHandler extends SimpleChannelInboundHandler<RemotingTransporter> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingTransporter msg)
        throws Exception {
      processMessageReceived(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      processChannelInactive(ctx);
    }
  }
}

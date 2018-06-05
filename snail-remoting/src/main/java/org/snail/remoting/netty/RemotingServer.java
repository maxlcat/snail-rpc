package org.snail.remoting.netty;

import java.nio.channels.Channel;
import java.util.concurrent.ExecutorService;
import org.snail.remoting.model.NettyChannelInactiveProcessor;
import org.snail.remoting.model.NettyRequestProcessor;
import org.snail.remoting.model.RemotingTransporter;

/**
 * Created by maxlcat on 2018/6/4.
 */
public interface RemotingServer extends BaseRemotingService{

  RemotingTransporter invokeSync(final Channel channel, final RemotingTransporter request, final long timeoutMillis);

  void registerProcessor(final byte requestCode, final NettyRequestProcessor processor, final ExecutorService executorService);

  void registerChannelInactiveProcessor(final NettyChannelInactiveProcessor processor, final ExecutorService executorService);

  void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executorService);




}

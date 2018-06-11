package org.snail.remoting.netty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chunjing.mu
 * @date: 2018/6/11
 **/
@Setter
@Getter
public class NettyClientConfig {

  private int clientWorkerThreads = 4;
  private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
  private long connectTimeoutMillis = 3000;
  private long channelNotActiveInterval = 1000 * 60;

  //format host:port,host:port
  private String defaultAddress;

  private int clientChannelMaxIdleTimeSeconds = 120;

  private int clientSocketSndBufSize = -1;
  private int clientSocketRcvBufSize = -1;

  private int writeBufferLowWaterMark = -1;
  private int writeBufferHighWaterMark = -1;

}

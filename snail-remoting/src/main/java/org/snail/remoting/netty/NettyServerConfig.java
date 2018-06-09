package org.snail.remoting.netty;

import lombok.Getter;
import lombok.Setter;
import org.snail.common.utils.Constants;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
@Setter
@Getter
public class NettyServerConfig implements Cloneable{

  private int listenPort = 8888;
  private int serverWorkerThreads = Constants.AVAILABLE_PROCESSORS;
  private int channelInactiveHandlerThreads = 1;
  private int serverSocketSendBufSize = -1;
  private int serverSocketRecBufSize = -1;
  private int writeBufferLowWaterMark = -1;
  private int writeBufferHighWaterMark = -1;

}

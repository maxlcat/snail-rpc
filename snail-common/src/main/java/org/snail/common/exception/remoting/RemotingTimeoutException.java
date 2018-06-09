package org.snail.common.exception.remoting;

/**
 * Created by maxlcat on 2018/6/6.
 */
public class RemotingTimeoutException extends RuntimeException{

  public RemotingTimeoutException(String address, long timeoutMills, Throwable cause) {
    super("wait response on the channel <" + address + "> timeout, " + timeoutMills + "(ms)", cause);
  }

    
}

package org.snail.common.exception.remoting;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
public class RemotingException extends Exception{

  public RemotingException(String message) {
    super(message);
  }

  public RemotingException(String message, Throwable cause) {
    super(message, cause);
  }
}

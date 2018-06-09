package org.snail.common.exception.remoting;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
public class RemotingNoSignException extends RemotingException{

  public RemotingNoSignException(String message) {
    super(message);
  }

  public RemotingNoSignException(String message, Throwable cause) {
    super(message, cause);
  }
}

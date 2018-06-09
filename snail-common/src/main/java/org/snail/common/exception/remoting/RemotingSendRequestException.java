package org.snail.common.exception.remoting;

/**
 * Created by maxlcat on 2018/6/6.
 */
public class RemotingSendRequestException extends RemotingException{

  public RemotingSendRequestException(String address) {
    super(address, null);
  }

  public RemotingSendRequestException(String address, Throwable cause) {
    super("send request to <" + address + "> failed", cause);
  }
}

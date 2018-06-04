package org.snail.common.exception.remoting;

/**
 * Created by maxlcat on 2018/6/4.
 */
public class RemotingContextException extends RuntimeException{

    public RemotingContextException(String message) {
        super(message);
    }

    public RemotingContextException(String message, Throwable cause) {
        super(message, cause);
    }
}

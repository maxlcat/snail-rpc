package org.snail.common.exception.remoting;

/**
 * Created by maxlcat on 2018/6/3.
 */
public class RemotingCommonCustomException extends RuntimeException {

    public RemotingCommonCustomException(String message) {
        super(message, null);
    }

    public RemotingCommonCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}

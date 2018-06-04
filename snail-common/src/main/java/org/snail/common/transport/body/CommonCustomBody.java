package org.snail.common.transport.body;

import org.snail.common.exception.remoting.RemotingCommonCustomException;

/**
 * Created by maxlcat on 2018/6/3.
 */
public interface CommonCustomBody {

    void checkFields() throws RemotingCommonCustomException;
}

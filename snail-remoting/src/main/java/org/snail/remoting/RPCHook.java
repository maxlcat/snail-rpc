package org.snail.remoting;

import org.snail.remoting.model.RemotingTransporter;

/**
 * Created by maxlcat on 2018/6/4.
 */
public interface RPCHook {

    void doBeforeRequest(final String remotingAddress, final RemotingTransporter request);

    void doAfterResponse(final String remotingAddress, final RemotingTransporter request, final RemotingTransporter response);
}

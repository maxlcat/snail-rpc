package org.snail.remoting;

import org.snail.remoting.model.RemotingResponse;

/**
 * @author chunjing.mu
 * @date: 2018/6/5
 **/
public interface InvokeCallback {

  void operationComplete(final RemotingResponse response);

}

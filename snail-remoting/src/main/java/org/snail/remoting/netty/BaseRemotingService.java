package org.snail.remoting.netty;

import org.snail.remoting.RPCHook;

/**
 * Created by maxlcat on 2018/6/4.
 */
public interface BaseRemotingService {

    void init();

    void start();

    void shutdown();

    void registerPRCHook(RPCHook rpcHook);
}

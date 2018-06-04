package org.snail.remoting.netty;

import org.snail.remoting.model.NettyChannelInactiveProcessor;
import org.snail.remoting.model.NettyRequestProcessor;
import org.snail.remoting.model.RemotingTransporter;

import java.util.concurrent.ExecutorService;

/**
 * Created by maxlcat on 2018/6/4.
 */
public interface RemotingClient extends BaseRemotingService{

    /**
     *
     * @param address
     * @param request
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    RemotingTransporter invokeSync(final String address, final RemotingTransporter request, final long timeoutMillis)
            throws InterruptedException;

    void registerProcessor(final byte requestCode, final NettyRequestProcessor processor, final ExecutorService executor);

    void registerChannelInactiveProcessor(NettyChannelInactiveProcessor processor, ExecutorService executor);

    boolean isChannelWriteAble(final String address);

    void activeConnect(boolean isReconnect);
}

package org.snail.remoting.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by maxlcat on 2018/6/4.
 */
public interface NettyRequestProcessor {

    RemotingTransporter processRequest(ChannelHandlerContext ctx, RemotingTransporter request) throws Exception;
}

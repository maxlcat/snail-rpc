package org.snail.remoting.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by maxlcat on 2018/6/4.
 */
public interface NettyChannelInactiveProcessor {

    void processChannelInactive(ChannelHandlerContext ctx) throws InterruptedException;
}

package org.snail.remoting.netty.idle;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.snail.common.exception.remoting.RemotingNoSignException;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
@Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter{

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    System.out.println("Accept heartbeat");
    if (evt instanceof IdleStateEvent) {
      IdleState state = ((IdleStateEvent) evt).state();
      if (state == IdleState.READER_IDLE) {
        throw new RemotingNoSignException("No sign");
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}

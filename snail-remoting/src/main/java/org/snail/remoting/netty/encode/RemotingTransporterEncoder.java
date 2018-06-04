package org.snail.remoting.netty.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.snail.common.protocal.SnailProtocol;
import org.snail.remoting.model.RemotingTransporter;

import static org.snail.common.serialization.SerializerHolder.serializerImpl;

/**
 * Created by maxlcat on 2018/6/3.
 */
public class RemotingTransporterEncoder extends MessageToByteEncoder<RemotingTransporter>{

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RemotingTransporter msg, ByteBuf out) throws Exception {
        deEncodeRemotingTransporter(msg, out);
    }

    private void deEncodeRemotingTransporter(RemotingTransporter msg, ByteBuf out) {
        byte[] body = serializerImpl().writeObject(msg.getCustomBody());
        byte isCompress = SnailProtocol.UNCOMPRESS;

        out.writeShort(SnailProtocol.MAGIC)
                .writeByte(msg.getTransporterType())
                .writeByte(msg.getCode())
                .writeByte(isCompress)
                .writeInt(body.length)
                .writeLong(msg.getOpaque())
                .writeBytes(body);

    }
}

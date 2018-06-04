package org.snail.remoting.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snail.common.exception.remoting.RemotingContextException;
import org.snail.common.protocal.SnailProtocol;

import java.util.List;
import org.snail.remoting.model.RemotingTransporter;
import org.xerial.snappy.Snappy;


/**
 * Created by maxlcat on 2018/6/3.
 */
public class RemotingTransporterDecoder extends ReplayingDecoder<RemotingTransporterDecoder.State>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotingTransporterDecoder.class);

    private static final int MAX_BODY_SIZE = 1024 * 1024 * 5;

    private final SnailProtocol header = new SnailProtocol();

    public RemotingTransporterDecoder() {
        super(State.HEADER_MAGIC);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case HEADER_MAGIC:
                checkMagic(in.readShort());
                checkpoint(State.HEADER_TYPE);
            case HEADER_TYPE:
                header.setType(in.readByte());
                checkpoint(State.HEADER_SIGN);
            case HEADER_SIGN:
                header.setSign(in.readByte());
                checkpoint(State.HEADER_ID);
            case HEADER_ID:
                header.setId(in.readLong());
                checkpoint(State.BODY_LENGTH);
            case BODY_LENGTH:
                header.setBodyLength(in.readInt());
                checkpoint(State.HEADER_COMPRESS);
            case HEADER_COMPRESS:
                header.setCompress(in.readByte());
                checkpoint(State.BODY);
            case BODY:
                int bodyLength = checkBodyLength(header.getBodyLength());
                byte[] bytes = new byte[bodyLength];
                in.readBytes(bytes);
                if (header.getCompress() == SnailProtocol.COMPRESS) {
                    bytes = Snappy.uncompress(bytes);
                }
                out.add(RemotingTransporter.newInstance(header.getId(), header.getSign(), header.getType(), bytes));
                break;
            default:
                break;
        }
        checkpoint(State.HEADER_MAGIC);
    }

    private void checkMagic(short magic) throws RemotingContextException {
        if (SnailProtocol.MAGIC != magic) {
            LOGGER.error("Magic is not match");
            throw new RemotingContextException("Magic value is not equal" + SnailProtocol.MAGIC);
        }
    }

    private int checkBodyLength(int length) {
        if (length > MAX_BODY_SIZE) {
            throw new RemotingContextException("The body's length is bigger than limit value " + MAX_BODY_SIZE);
        }
        return length;
    }

    enum State {
        HEADER_MAGIC, HEADER_TYPE, HEADER_SIGN, HEADER_ID, BODY_LENGTH, HEADER_COMPRESS, BODY
    }
}

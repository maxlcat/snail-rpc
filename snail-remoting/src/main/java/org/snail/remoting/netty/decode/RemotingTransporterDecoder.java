package org.snail.remoting.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snail.common.exception.remoting.RemotingContextException;
import org.snail.common.protocal.SnailProtocol;

import java.util.List;


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
                checkpoint(State.HEADER_MAGIC);
            case HEADER_TYPE:
                header.

        }
    }

    private void checkMagic(short magic) throws RemotingContextException {
        if (SnailProtocol.MAGIC != magic) {
            LOGGER.error("Magic is not match");
            throw new RemotingContextException("Magic value is not equal" + SnailProtocol.MAGIC);
        }
    }

    enum State {
        HEADER_MAGIC, HEADER_TYPE, HEADER_SIGN, HEADER_ID, HEADER_BODY, HEADER_LENGTH, HEADER_COMPRESS, BODY;
    }
}

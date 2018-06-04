package org.snail.remoting.model;

import org.snail.common.transport.body.CommonCustomBody;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @auther: mcj
 * @description:
 * @date: 2017/10/13
 **/
public class RemotingTransporter extends ByteHolder{

    private static final AtomicLong REQUEST_ID = new AtomicLong(0l);

    private byte code;

    private transient CommonCustomBody customBody;

    private transient long timestamp;

    private long opaque = REQUEST_ID.getAndIncrement();

    private byte transporterType;

    public RemotingTransporter() {
    }

    public static RemotingTransporter newInstance(long id, byte sign, byte type, byte[] bytes) {
        RemotingTransporter remotingTransporter = new RemotingTransporter();
        remotingTransporter.setOpaque(id);
        remotingTransporter.setCode(sign);
        remotingTransporter.setTransporterType(type);
        remotingTransporter.setBytes(bytes);
        return remotingTransporter;
    }

    public static AtomicLong getRequestId() {
        return REQUEST_ID;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public CommonCustomBody getCustomBody() {
        return customBody;
    }

    public void setCustomBody(CommonCustomBody customBody) {
        this.customBody = customBody;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public byte getTransporterType() {
        return transporterType;
    }

    public void setTransporterType(byte transporterType) {
        this.transporterType = transporterType;
    }

    @Override
    public String toString() {
        return "RemotingTransporter{" +
                "code=" + code +
                ", customBody=" + customBody +
                ", timestamp=" + timestamp +
                ", opaque=" + opaque +
                ", transporterType=" + transporterType +
                '}';
    }
}

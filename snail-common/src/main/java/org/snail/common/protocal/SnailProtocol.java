package org.snail.common.protocal;

/**
 * Created by maxlcat on 2018/6/3.
 */
public class SnailProtocol {

    public static final int HEADER_LENGTH = 16;

    public static final short MAGIC = (short) 0xba;

    public static final byte REQUEST_REMOTING = 1;

    public static final byte RESPONSE_REMOTING = 2;

    public static final byte UNCOMPRESS = 81;

    public static final byte COMPRESS = 82;

    private byte type;

    private byte sign;

    private long id;

    private int bodyLength;

    private byte compress;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte getCompress() {
        return compress;
    }

    public void setCompress(byte compress) {
        this.compress = compress;
    }
}

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

}

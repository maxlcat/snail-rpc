package org.snail.remoting.model;

/**
 * @author chunjing.mu
 * @date: 2018/6/4
 **/
public class ByteHolder {

  private transient byte[] bytes;

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public int size() {
    return bytes == null ? 0 : bytes.length;
  }


}

package org.snail.common.utils;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
public final class NativeSupport {

  private static final boolean SUPPORT_NATIVE_ET;

  static {
    boolean epoll;
    try {
      Class.forName("io.netty.channel.epoll.Native");
      epoll = true;
    } catch (ClassNotFoundException e) {
      epoll = false;
    }
    SUPPORT_NATIVE_ET = epoll;
  }

  public static boolean isSupportNativeET() {
    return SUPPORT_NATIVE_ET;
  }
}

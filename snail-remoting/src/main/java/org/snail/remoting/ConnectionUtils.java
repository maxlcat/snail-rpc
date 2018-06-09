package org.snail.remoting;

import io.netty.channel.Channel;
import java.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
@Slf4j
public class ConnectionUtils {

  public static String parseChannelRemoeAddress(final Channel channel) {
    if (channel == null)
      return "";
    final SocketAddress remote = channel.remoteAddress();
    final String address = remote != null ? remote.toString() : "";
    if (address.length() > 0) {
      int index = address.lastIndexOf("/");
      if (index >= 0) {
        return address.substring(index + 1);
      }
      return address;
    }
    return "";
  }

  public static Address
}

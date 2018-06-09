package org.snail.remoting.netty.idle;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import java.util.concurrent.TimeUnit;
import org.snail.common.utils.SystemClock;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
public class IdleStateChecker extends ChannelDuplexHandler{

  private static final long MIN_TIMEOUT_MILLS = 1;

  private final HashedWheelTimer timer;
  private final long readerIdleTimeMills;
  private final long writerIdleTimeMills;
  private final long allIdleTimeMills;

  private volatile int state;
  private volatile boolean reading;
  private volatile Timeout readerIdleTimeout;
  private volatile long lastReadTime;
  private boolean firstReaderIdleEvent = true;
  private volatile Timeout writerIdleTimeout;
  private volatile long lastWriteTime;
  private boolean firstWriterIdleEvent = true;

  private volatile Timeout allIdleTimeout;
  private boolean firstAllIdleEvent = true;

  private final ChannelFutureListener writeListener = (future -> {
    firstWriterIdleEvent = firstAllIdleEvent = true;
    lastWriteTime = SystemClock.millisClock().now();
  });

  public IdleStateChecker(HashedWheelTimer timer, long readerIdleTimeMills,
      long writerIdleTimeMills,
      long allIdleTimeMills) {
    this(timer, readerIdleTimeMills, writerIdleTimeMills, allIdleTimeMills, TimeUnit.SECONDS);
  }

  public IdleStateChecker(HashedWheelTimer timer, long readerIdleTimeMills,
      long writerIdleTimeMills,
      long allIdleTimeMills,
      TimeUnit unit) {
    if (unit == null) {
      throw new NullPointerException("unit");
    }
    if (readerIdleTimeMills <= 0) {
      this.readerIdleTimeMills = 0;
    } else {
      this.readerIdleTimeMills = Math.max(unit.toMillis(readerIdleTimeMills), MIN_TIMEOUT_MILLS);
    }
    if (writerIdleTimeMills <= 0) {
      this.writerIdleTimeMills = 0;
    } else {
      this.writerIdleTimeMills = Math.max(unit.toMillis(writerIdleTimeMills), MIN_TIMEOUT_MILLS);
    }
    if (allIdleTimeMills <= 0) {
      this.allIdleTimeMills = 0;
    } else {
      this.allIdleTimeMills = Math.max(unit.toMillis(allIdleTimeMills), MIN_TIMEOUT_MILLS);
    }
    this.timer = timer;
  }

}

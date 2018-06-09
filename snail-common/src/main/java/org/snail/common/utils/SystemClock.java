package org.snail.common.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
public class SystemClock {

  private final long precision;
  private final AtomicLong now;

  private static final SystemClock millisClock = new SystemClock(1);

  private SystemClock(long precision) {
    this.precision = precision;
    now = new AtomicLong(System.currentTimeMillis());
  }

  private void scheduleClockUpdating() {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor((runnable) -> {
      Thread thread = new Thread(runnable, "system.clock");
      thread.setDaemon(true);
      return thread;
    });
    scheduledExecutorService.scheduleAtFixedRate(() -> {
      now.set(System.currentTimeMillis());
    }, precision, precision, TimeUnit.MILLISECONDS);
  }

  public static SystemClock millisClock() {
    return millisClock;
  }

  public long now() {
    return now.get();
  }

  public long precision() {
    return precision;
  }

}

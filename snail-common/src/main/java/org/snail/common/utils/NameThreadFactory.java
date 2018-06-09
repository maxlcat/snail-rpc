package org.snail.common.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
@Slf4j
public class NameThreadFactory implements ThreadFactory{

  private static final AtomicInteger poolId = new AtomicInteger();

  private final String prefix;
  private final AtomicInteger nextId = new AtomicInteger();
  private final boolean daemon;
  private final ThreadGroup group;

  public NameThreadFactory() {
    this("pool-" + poolId.incrementAndGet(), false);
  }

  public NameThreadFactory(String prefix, boolean daemon) {
    this.prefix = prefix + "#";
    this.daemon = daemon;
    SecurityManager s = System.getSecurityManager();
    group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
  }

  public NameThreadFactory(String prefix) {
    this(prefix, false);
  }

  @Override
  public Thread newThread(Runnable r) {
    String name = prefix + nextId.getAndIncrement();
    Thread thread = new Thread(group,r, name);
    try {
      if (thread.isDaemon()) {
        if (!daemon) {
          thread.setDaemon(false);
        }
      } else {
        if (daemon) {
          thread.setDaemon(true);
        }
      }
    } catch (Exception e) {

    }
    log.debug("create new {}", thread);
    return thread;
  }

  public ThreadGroup getGroup() {
    return group;
  }
}

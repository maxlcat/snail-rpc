package org.snail.remoting.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.snail.remoting.InvokeCallback;

/**
 * @author chunjing.mu
 * @date: 2018/6/5
 **/
public class RemotingResponse {

  private volatile RemotingTransporter transporter;

  private volatile Throwable cause;

  private volatile boolean sendRequestOk = true;

  private final long opaque;

  private final InvokeCallback invokeCallback;

  private final long timeoutMills;

  private final long beginTimestamp = System.currentTimeMillis();

  private final CountDownLatch countDownLatch = new CountDownLatch(1);

  public RemotingResponse(long opaque, InvokeCallback invokeCallback, long timeoutMills) {
    this.opaque = opaque;
    this.invokeCallback = invokeCallback;
    this.timeoutMills = timeoutMills;
  }

  public RemotingTransporter getTransporter() {
    return transporter;
  }

  public void setTransporter(RemotingTransporter transporter) {
    this.transporter = transporter;
  }

  public Throwable getCause() {
    return cause;
  }

  public void setCause(Throwable cause) {
    this.cause = cause;
  }

  public boolean isSendRequestOk() {
    return sendRequestOk;
  }

  public long getOpaque() {
    return opaque;
  }

  public long getTimeoutMills() {
    return timeoutMills;
  }

  public RemotingTransporter waitResponse() throws InterruptedException {
    countDownLatch.await(timeoutMills, TimeUnit.MILLISECONDS);
    return transporter;
  }

  public void putResponse(final RemotingTransporter transporter) {
    this.transporter = transporter;
    countDownLatch.countDown();
  }

  public void setSendRequestOk(boolean sendRequestOk) {
    this.sendRequestOk = sendRequestOk;
  }
}

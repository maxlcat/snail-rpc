package org.snail.remoting.netty;

import java.util.concurrent.ExecutorService;
import org.snail.remoting.RPCHook;
import org.snail.remoting.model.NettyChannelInactiveProcessor;
import org.snail.remoting.model.NettyRequestProcessor;
import org.snail.remoting.model.RemotingTransporter;

/**
 * @author chunjing.mu
 * @date: 2018/6/9
 **/
public class NettyRemotingClient extends NettyRemotingBase implements RemotingClient{

  @Override
  protected RPCHook getRPCHook() {
    return null;
  }

  @Override
  public RemotingTransporter invokeSync(String address, RemotingTransporter request,
      long timeoutMillis) throws InterruptedException {
    return null;
  }

  @Override
  public void registerProcessor(byte requestCode, NettyRequestProcessor processor,
      ExecutorService executor) {

  }

  @Override
  public void registerChannelInactiveProcessor(NettyChannelInactiveProcessor processor,
      ExecutorService executor) {

  }

  @Override
  public boolean isChannelWriteAble(String address) {
    return false;
  }

  @Override
  public void activeConnect(boolean isReconnect) {

  }

  @Override
  public void init() {

  }

  @Override
  public void start() {

  }

  @Override
  public void shutdown() {

  }

  @Override
  public void registerPRCHook(RPCHook rpcHook) {

  }
}

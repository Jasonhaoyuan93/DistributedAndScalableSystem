package edu.neu.cs6650.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool.BasePoolableObjectFactory;

public class RMQChannelFactory extends BasePoolableObjectFactory<Channel> {

  private Connection rmqConnection;

  public RMQChannelFactory(Connection rmqConnection) {
    this.rmqConnection = rmqConnection;
  }

  @Override
  public Channel makeObject() throws Exception {
    return rmqConnection.createChannel();
  }
}

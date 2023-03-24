package edu.neu.cs6650.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class RMQChannelFactory implements PooledObjectFactory<Channel> {

  private static final String EXCHANGE_NAME = "SwipeEventExchange";
  private Connection rmqConnection;

  public RMQChannelFactory(Connection rmqConnection) {
    this.rmqConnection = rmqConnection;
  }

  @Override
  public void activateObject(PooledObject<Channel> pooledObject) throws Exception {
    pooledObject.getObject().exchangeDeclare(EXCHANGE_NAME, "topic");
  }

  @Override
  public void destroyObject(PooledObject<Channel> pooledObject) throws Exception {
    pooledObject.getObject().close();
  }

  @Override
  public PooledObject<Channel> makeObject() throws Exception {
    Channel rmqChannel = rmqConnection.createChannel();
    return new DefaultPooledObject<>(rmqChannel);
  }

  @Override
  public void passivateObject(PooledObject<Channel> pooledObject) throws Exception {
  }

  @Override
  public boolean validateObject(PooledObject<Channel> pooledObject) {
    if(pooledObject.getObject()==null){
      return false;
    }else{
      return pooledObject.getObject().isOpen();
    }
  }
}

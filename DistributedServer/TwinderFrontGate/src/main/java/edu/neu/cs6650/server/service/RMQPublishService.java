package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.cs6650.server.RMQChannelFactory;
import edu.neu.cs6650.server.model.Request;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

public class RMQPublishService {

  private static final String EXCHANGE_NAME = "SwipeEventExchange";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private RMQChannelFactory rmqChannelFactory;
  private ObjectPool<Channel> rmqChannelPool;

  public RMQPublishService() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setUsername("guest");
    factory.setPassword("guest");
    rmqChannelFactory = new RMQChannelFactory(factory.newConnection());
    rmqChannelPool = new GenericObjectPool<>(rmqChannelFactory,200);
  }

  public void publishMessage(Request request) throws Exception {
    Channel rmqChannel = null;
    try{
      rmqChannel =  rmqChannelPool.borrowObject();
      rmqChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
      String routingKey = request.isSwipeRight()?"right.":"left." + "swipe";
      String message = OBJECT_MAPPER.writeValueAsString(request);
      rmqChannel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
    }finally {
      try {
        if (rmqChannel!=null) {
          rmqChannelPool.returnObject(rmqChannel);
        }
      } catch (Exception e) {
        // ignored
      }
    }
  }
}

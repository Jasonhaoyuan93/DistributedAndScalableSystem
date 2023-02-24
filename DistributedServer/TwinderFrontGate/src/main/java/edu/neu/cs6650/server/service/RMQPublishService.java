package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.cs6650.server.RMQChannelFactory;
import edu.neu.cs6650.server.model.Request;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

public class RMQPublishService {

  private static final String EXCHANGE_NAME = "SwipeEventExchange";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final ObjectPool<Channel> rmqChannelPool;

  public RMQPublishService() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("34.222.62.18");
    factory.setUsername("jasonhaoyuan");
    factory.setPassword("Yh19930718!");
    RMQChannelFactory rmqChannelFactory = new RMQChannelFactory(factory.newConnection());
    rmqChannelPool = new GenericObjectPool<>(rmqChannelFactory,200);
  }

  public void publishMessage(Request request) throws Exception {
    Channel rmqChannel = null;
    try{
      rmqChannel =  rmqChannelPool.borrowObject();
      rmqChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
      String routingKey = "swipe." + (request.isSwipeRight()?"right":"left");
      request.setStartTime(System.currentTimeMillis());
      String message = OBJECT_MAPPER.writeValueAsString(request);
//      System.out.println(" [+] Publishing message to %s with routingKey=%s.".formatted(EXCHANGE_NAME,routingKey));
      rmqChannel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(
          StandardCharsets.UTF_8));
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

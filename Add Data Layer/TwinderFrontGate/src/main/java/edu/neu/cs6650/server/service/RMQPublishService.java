package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.cs6650.server.RMQChannelFactory;
import edu.neu.cs6650.server.model.Request;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


public class RMQPublishService implements AutoCloseable{

  private static final String EXCHANGE_NAME = "SwipeEventExchange";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final GenericObjectPool<Channel> rmqChannelPool;
  private final Connection rmqConnection;

  public RMQPublishService() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(System.getProperty("rmqHost","127.0.0.1"));
    factory.setUsername(System.getProperty("rmqUsername","guest"));
    factory.setPassword(System.getProperty("rmqPassword","guest"));
    rmqConnection = factory.newConnection();
    RMQChannelFactory rmqChannelFactory = new RMQChannelFactory(rmqConnection);
    GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    genericObjectPoolConfig.setMaxIdle(200);
    genericObjectPoolConfig.setBlockWhenExhausted(true);
    genericObjectPoolConfig.setTestOnBorrow(true);
    genericObjectPoolConfig.setTestWhileIdle(true);
    rmqChannelPool = new GenericObjectPool<>(rmqChannelFactory, genericObjectPoolConfig);
    rmqChannelPool.setMaxTotal(200);
  }

  public void publishMessage(Request request) throws Exception {
    Channel rmqChannel = null;
    try{
      rmqChannel =  rmqChannelPool.borrowObject();
      String routingKey = "swipe." + (request.isSwipeRight()?"right":"left");
      request.setStartTime(System.currentTimeMillis());
      String message = OBJECT_MAPPER.writeValueAsString(request);
//      System.out.println(" [+] Publishing message to %s with routingKey=%s.".formatted(EXCHANGE_NAME,routingKey));
      long startTime = System.currentTimeMillis();
      rmqChannel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(
          StandardCharsets.UTF_8));
      System.out.println("Published to rabbitMQ with elapsed time: %d".formatted(System.currentTimeMillis()-startTime));
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

  @Override
  public void close() throws Exception {
    rmqConnection.close();
  }
}

package edu.neu.cs6650.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.cs6650.client.Handler.MessageHandler;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class TwinderStatisticDriver {

  private static final String EXCHANGE_NAME = "SwipeEventExchange";
  private static final String QUEUE_NAME = "SwipeStatisticsQueue";

  private static Channel rmqChannel;

  public static void main(String[] args) throws Exception {
    String host = args[0];
    String bindingKey = args[1];
    int prefetchCount = Integer.parseInt(args[2]);
    int threadPoolSize = Integer.parseInt(args[3]);
    String username = args[4];
    String password = args[5];

    rmqConnectionSetup(host, prefetchCount, username, password);
    System.out.printf(
        " [*] Twinder Statistic Driver initialized with Exchange key=\"%s\", Binding key=\"%s\"%n",
        EXCHANGE_NAME, bindingKey);

    rmqChannel.queueBind(QUEUE_NAME, EXCHANGE_NAME, bindingKey);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    ExecutorService executorService = new ScheduledThreadPoolExecutor(threadPoolSize*6);

    MessageHandler messageHandler = new MessageHandler(executorService);
    for (int i = 0; i < threadPoolSize; i++) {
      rmqChannel.basicConsume(QUEUE_NAME, true, messageHandler, consumerTag -> {
      });
    }
  }

  private static void rmqConnectionSetup(String host, int prefetchCount, String username, String password) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setUsername(username);
    factory.setPassword(password);
    Connection connection = factory.newConnection();

    rmqChannel = connection.createChannel();
    rmqChannel.basicQos(prefetchCount);
    rmqChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
    rmqChannel.queueDeclare(QUEUE_NAME, true, false, false, null);
  }
}
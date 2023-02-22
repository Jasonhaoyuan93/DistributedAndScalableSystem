package edu.neu.cs6650.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


public class Worker {

  private final static String TASK_QUEUE_NAME = "hello";
  private static final String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
    String queueName = channel.queueDeclare().getQueue();
    for(String severity : argv){
      channel.queueBind(queueName, EXCHANGE_NAME, severity);
    }

//    channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    channel.basicQos(1);

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");

      System.out.println(" [x] Received '" + message + "'");
      try {
        doWork(message);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        System.out.println(" [x] Done");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
      }
    };
    boolean autoAck = false; // acknowledgment is covered below
    channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag -> { });
  }

  private static void doWork(String task) throws InterruptedException {
    for (char ch: task.toCharArray()) {
      if (ch == '.') Thread.sleep(1000);
    }
  }

}

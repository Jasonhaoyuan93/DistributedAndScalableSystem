package edu.neu.cs6650.client.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import edu.neu.cs6650.client.dao.DBPersistThread;
import edu.neu.cs6650.client.model.Request;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class MessageHandler implements DeliverCallback {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final ExecutorService executorService;

  public MessageHandler(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void handle(String consumerTag, Delivery message) throws IOException {
    Request request = OBJECT_MAPPER.readValue(message.getBody(), Request.class);
    DBPersistThread dbPersistThread = new DBPersistThread(request);
    executorService.submit(dbPersistThread);
  }
}

package edu.neu.cs6650.client.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import edu.neu.cs6650.client.dao.TwinderDAO;
import edu.neu.cs6650.client.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class MessageHandler implements DeliverCallback {

  private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final TwinderDAO twinderDAO = TwinderDAO.getInstance();

  @Override
  public void handle(String consumerTag, Delivery message) throws IOException {
    Request request = OBJECT_MAPPER.readValue(message.getBody(), Request.class);
    long startTime = System.currentTimeMillis();
    try {
      twinderDAO.updateStatsOnUser(Integer.parseInt(request.getSwipee()), request.isSwipeRight());
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error("User match statistics insertion failed with cause: "+e.getMessage());
    }
    System.out.printf(" [+] Message consumed with DB latency = %d ms and overall latency: %d ms.%n",
            System.currentTimeMillis() - startTime, System.currentTimeMillis() - request.getStartTime());
  }
}

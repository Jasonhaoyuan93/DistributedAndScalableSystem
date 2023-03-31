package edu.neu.cs6650.client.dao;

import edu.neu.cs6650.client.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBPersistThread implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(DBPersistThread.class);
  private TwinderDAO twinderDAO;
  private Request request;

  public DBPersistThread(Request request) {
    this.request = request;
    this.twinderDAO = "true".equalsIgnoreCase(System.getProperty("DynamoDBEnabled", "false"))
        ? TwinderDynamodbDAO.getInstance() : TwinderMysqlDAO.getInstance();
  }

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    try {
      twinderDAO.updateStatsOnUser(Integer.parseInt(request.getSwipee()), request.isSwipeRight());
    } catch (Exception e) {
      logger.error("User match statistics insertion failed with cause: %s",e.getMessage());
    }
    System.out.printf(" [+] Message consumed with DB latency = %d ms and overall latency: %d ms.%n",
        System.currentTimeMillis() - startTime, System.currentTimeMillis() - request.getStartTime());
  }
}

package org.neu.cs6650.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.neu.cs6650.client.model.Request;
import org.neu.cs6650.client.service.HttpService;
import org.neu.cs6650.client.service.SummaryService;

public class Main {

  private static final String ROUTE_LEFT = "/Server_war/twinder/swipe/left";
  private static final String ROUTE_RIGHT = "/Server_war/twinder/swipe/right";
  private static final Random random = new Random();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final boolean isPartTwoEnabled = true;

  public static void main(String[] args) {

    String host = args[0];
    int threadCount = Integer.parseInt(args[1]);
    int requestCount = Integer.parseInt(args[2]);

    ExecutorService executorService = new ScheduledThreadPoolExecutor(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(requestCount);

    try (HttpService httpService = new HttpService(threadCount,
        host); SummaryService summaryService = new SummaryService(new File("./out"), isPartTwoEnabled)) {
      for (int i = 0; i < requestCount; i++) {
        if (random.nextBoolean()) {
          executorService.execute(new RequestThread(httpService, summaryService,
              objectMapper.writeValueAsString(new Request(random)), countDownLatch, ROUTE_LEFT,
              isPartTwoEnabled));
        } else {
          executorService.execute(new RequestThread(httpService, summaryService,
              objectMapper.writeValueAsString(new Request(random)), countDownLatch, ROUTE_RIGHT,
              isPartTwoEnabled));
        }
      }
      executorService.shutdown();
      countDownLatch.await();
      summaryService.summarize(requestCount);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
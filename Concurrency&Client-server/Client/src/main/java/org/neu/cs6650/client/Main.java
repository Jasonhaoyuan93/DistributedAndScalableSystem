package org.neu.cs6650.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
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

  public static void main(String[] args) throws InterruptedException, IOException {

    String host = args[0];
    int connectionCount = Integer.parseInt(args[1]);
    int threadCount = Integer.parseInt(args[2]);
    int requestCount = Integer.parseInt(args[3]);

    ExecutorService executorService = new ScheduledThreadPoolExecutor(threadCount);

    try(HttpService leftHttpService = new HttpService(connectionCount / 2, host, ROUTE_LEFT);
    HttpService rightHttpService = new HttpService(connectionCount / 2, host, ROUTE_RIGHT);
    SummaryService summaryService = new SummaryService(new File("./out"))){
      CountDownLatch countDownLatch = new CountDownLatch(requestCount);
      long startTime = System.currentTimeMillis();
      for (int i = 0; i < requestCount; i++) {
        if (random.nextBoolean()) {
          executorService.execute(new RequestThread(leftHttpService, summaryService,
              objectMapper.writeValueAsString(new Request(random)), countDownLatch, random));
        } else {
          executorService.execute(new RequestThread(rightHttpService, summaryService,
              objectMapper.writeValueAsString(new Request(random)), countDownLatch,random));
        }
      }
      executorService.shutdown();
      countDownLatch.await();
      summaryService.summarize(startTime, requestCount);
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
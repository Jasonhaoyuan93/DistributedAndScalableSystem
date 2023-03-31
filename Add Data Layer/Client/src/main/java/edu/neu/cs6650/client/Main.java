package edu.neu.cs6650.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.client.model.PostRequest;
import edu.neu.cs6650.client.service.HttpService;
import edu.neu.cs6650.client.service.SummaryService;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main {

  private static final String ROUTE_LEFT = "/TwinderFrontGate_war/twinder/swipe/left";
  private static final String ROUTE_RIGHT = "/TwinderFrontGate_war/twinder/swipe/right";
  private static final Random random = new Random();
  private static final ObjectMapper objectMapper = new ObjectMapper();


  public static void main(String[] args) {

    String host = args[0];
    int threadCount = Integer.parseInt(args[1]);
    int requestCount = Integer.parseInt(args[2]);

    ExecutorService executorService = new ScheduledThreadPoolExecutor(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(requestCount);
    try (HttpService httpService = new HttpService(threadCount,
        host)) {
      SummaryService summaryService = new SummaryService();
      GetThread getThread = new GetThread(httpService, summaryService);
      Timer timer = new Timer();
      timer.schedule(getThread, 100, 200);
      for (int i = 0; i < requestCount; i++) {
        if (random.nextBoolean()) {
          executorService.execute(new RequestThread(httpService, summaryService,
              objectMapper.writeValueAsString(new PostRequest(random)), countDownLatch, ROUTE_LEFT));
        } else {
          executorService.execute(new RequestThread(httpService, summaryService,
              objectMapper.writeValueAsString(new PostRequest(random)), countDownLatch, ROUTE_RIGHT));
        }
      }
      executorService.shutdown();
      countDownLatch.await();
      timer.cancel();
      summaryService.summarize(requestCount);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
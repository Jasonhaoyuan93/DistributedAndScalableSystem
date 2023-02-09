package org.neu.cs6650.client;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.neu.cs6650.client.model.Response;
import org.neu.cs6650.client.service.HttpService;
import org.neu.cs6650.client.service.SummaryService;

public class RequestThread implements Runnable {

  private static final Random random = new Random();

  private HttpService httpService;
  private SummaryService summaryService;
  private String requestBody;
  private String path;
  private CountDownLatch countDownLatch;
  private boolean isPartTwoEnabled;

  public RequestThread(HttpService httpService, SummaryService summaryService, String requestBody,
      CountDownLatch countDownLatch, String path, boolean isPartTwoEnabled) {
    this.httpService = httpService;
    this.summaryService = summaryService;
    this.requestBody = requestBody;
    this.countDownLatch = countDownLatch;
    this.path = path;
    this.isPartTwoEnabled = isPartTwoEnabled;
  }

  @Override
  public void run() {
    try {
      Response response = httpService.processHttpRequest(requestBody, path);
      if(isPartTwoEnabled){
        summaryService.addResponse(response);
      }
      countDownLatch.countDown();
      if(random.nextInt(50000)==1){
        System.out.println("Request processing, current remaining: %s".formatted(countDownLatch.getCount()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      countDownLatch.countDown();
      summaryService.addResponse(new Response());
      throw new RuntimeException(e);
    }
  }
}

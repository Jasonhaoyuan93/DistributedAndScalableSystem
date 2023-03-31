package edu.neu.cs6650.client;

import edu.neu.cs6650.client.model.PostResponse;
import edu.neu.cs6650.client.service.HttpService;
import edu.neu.cs6650.client.service.SummaryService;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class RequestThread implements Runnable {

  private static final Random random = new Random();

  private final HttpService httpService;
  private final SummaryService summaryService;
  private final String requestBody;
  private final String path;
  private final CountDownLatch countDownLatch;

  public RequestThread(HttpService httpService, SummaryService summaryService, String requestBody,
      CountDownLatch countDownLatch, String path) {
    this.httpService = httpService;
    this.summaryService = summaryService;
    this.requestBody = requestBody;
    this.countDownLatch = countDownLatch;
    this.path = path;
  }

  @Override
  public void run() {
    try {
      PostResponse postResponse = httpService.processHttpRequest(requestBody, path);
      summaryService.addPostResponse(postResponse);
      countDownLatch.countDown();
      if(random.nextInt(10000)==1){
        System.out.println("Request processing, current remaining: %s".formatted(countDownLatch.getCount()));
      }
    } catch (Exception e) {
      System.out.println("POST request failed with cause: "+e.getMessage());
      countDownLatch.countDown();
      summaryService.addPostResponse(new PostResponse());
    }
  }
}

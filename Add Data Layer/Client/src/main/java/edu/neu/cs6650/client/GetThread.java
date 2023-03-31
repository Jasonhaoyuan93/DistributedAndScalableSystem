package edu.neu.cs6650.client;

import edu.neu.cs6650.client.model.Response;
import edu.neu.cs6650.client.service.HttpService;
import edu.neu.cs6650.client.service.SummaryService;
import java.util.Random;
import java.util.TimerTask;

public class GetThread extends TimerTask {

  private final HttpService httpService;
  private final Random random;
  private final SummaryService summaryService;

  public GetThread(HttpService httpService, SummaryService summaryService) {
    this.httpService = httpService;
    this.random = new Random();
    this.summaryService = summaryService;
  }

  @Override
  public void run() {
    int uid = random.nextInt(1, 5001);
    try {
      Response matchResponse = random.nextBoolean() ? httpService.obtainMatchesById(uid)
          : httpService.obtainStatisticsById(uid);
      summaryService.addGetResponse(matchResponse);
    } catch (Exception e) {
      System.out.println("GET request failed with cause: " + e.getMessage());
    }
  }
}

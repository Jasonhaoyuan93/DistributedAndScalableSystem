package edu.neu.cs6650.client.service;

import edu.neu.cs6650.client.model.PostResponse;
import edu.neu.cs6650.client.model.Response;
import edu.neu.cs6650.client.model.TimeStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Median;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 * Class OutputCsv could output the result to according path.
 */
public class SummaryService{

  private final AtomicInteger successCount;
  private final AtomicInteger failedCount;
  private final List<TimeStore> postElapsedTimes;
  private final List<TimeStore> getElapsedTimes;
  private final long startTime;

  public SummaryService(){

    postElapsedTimes = Collections.synchronizedList(new ArrayList<>());
    getElapsedTimes = new ArrayList<>();

    successCount = new AtomicInteger(0);
    failedCount = new AtomicInteger(0);
    startTime = System.currentTimeMillis();
  }

  public void summarize(int count) {
    long totalElapsedTime = System.currentTimeMillis() - startTime;
    writePostReport(totalElapsedTime,count);
    writeGetReport();
  }

  public void addPostResponse(PostResponse postResponse) {
    if (postResponse.isSucceed()) {
      postElapsedTimes.add(new TimeStore(postResponse.getElapsedTime()));
      successCount.getAndIncrement();
    } else {
      failedCount.getAndIncrement();
    }
  }

  public void addGetResponse(Response response) {
    getElapsedTimes.add(new TimeStore(response.getElapsedTime()));
  }

  private void writePostReport(long totalElapsedTime, int count){
    System.out.println("==================Post Summary==================");
    System.out.println(
        "Succeed count: %d. Failure count: %d".formatted(successCount.get(), failedCount.get()));
    System.out.println("Overall elapsed time: %d ms".formatted(totalElapsedTime));
    System.out.println("Throughput: %d req/sec".formatted(count * 1000 / totalElapsedTime));
    System.out.println("Mean response time: %f ms".formatted(calculateMean(postElapsedTimes)));
    System.out.println("Median response time: %d ms".formatted(calculateMedian(postElapsedTimes)));
    System.out.println("P99 response time: %d ms".formatted(calculatePercentile(postElapsedTimes)));
    System.out.println("Min response time: %d ms".formatted(calculateMin(postElapsedTimes)));
    System.out.println("Max response time: %d ms".formatted(calculateMax(postElapsedTimes)));
  }

  private void writeGetReport(){
    System.out.println("==================Get Summary==================");
    System.out.println("Mean response time: %f ms".formatted(calculateMean(getElapsedTimes)));
    System.out.println("Median response time: %d ms".formatted(calculateMedian(getElapsedTimes)));
    System.out.println("Min response time: %d ms".formatted(calculateMin(getElapsedTimes)));
    System.out.println("Max response time: %d ms".formatted(calculateMax(getElapsedTimes)));
  }

  protected int calculateMedian(List<TimeStore> elapsedTime) {
    Median median = new Median();
    return (int) median.evaluate(
        elapsedTime.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected int calculatePercentile(List<TimeStore> elapsedTime) {
    Percentile percentile = new Percentile();
    percentile.setQuantile(99);
    return (int) percentile.evaluate(
        elapsedTime.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected int calculateMin(List<TimeStore> elapsedTime) {
    Min min = new Min();
    return (int) min.evaluate(
        elapsedTime.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected int calculateMax(List<TimeStore> elapsedTime) {
    Max max = new Max();
    return (int) max.evaluate(
        elapsedTime.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected double calculateMean(List<TimeStore> elapsedTime) {
    Mean mean = new Mean();
    return mean.evaluate(
        elapsedTime.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }
}

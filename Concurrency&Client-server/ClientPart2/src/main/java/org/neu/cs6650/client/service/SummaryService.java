package org.neu.cs6650.client.service;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Median;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.neu.cs6650.client.model.Response;
import org.neu.cs6650.client.model.TimeStore;

/**
 * Class OutputCsv could output the result to according path.
 */
public class SummaryService implements Closeable {

  private static final String OUTPUT_FILENAME_FORMAT = "%s/Store.csv";
  private static final String LINE_FORMAT = "%d,%d\n";
  private AtomicInteger successCount;
  private AtomicInteger failedCount;
  private List<TimeStore> elapsedTimes;
  private final BufferedWriter outputStream;
  private final long startTime;
  private final boolean isPartTwoEnabled;

  public SummaryService(File directory, boolean isPartTwoEnabled) throws IOException {
    directory.mkdirs();
    File outputFile = new File(OUTPUT_FILENAME_FORMAT.formatted(directory.getAbsolutePath()));
    if (outputFile.exists()) {
      outputFile.delete();
    }
    outputFile.createNewFile();
    elapsedTimes = Collections.synchronizedList(new LinkedList<>());
    outputStream = new BufferedWriter(new FileWriter(outputFile, true));
    successCount = new AtomicInteger(0);
    failedCount = new AtomicInteger(0);
    this.isPartTwoEnabled = isPartTwoEnabled;
    startTime = System.currentTimeMillis();
  }

  public void summarize(int count, int threadCount) {
    long totalElapsedTime = System.currentTimeMillis() - startTime;

    System.out.println("==================Summary==================");
    System.out.println(
        "Succeed count: %d. Failure count: %d".formatted(successCount.get(), failedCount.get()));
    System.out.println("Overall elapsed time: %d ms".formatted(totalElapsedTime));
    System.out.println("Throughput: %d req/sec".formatted(count*1000 / totalElapsedTime));

    if(isPartTwoEnabled){
      System.out.println("Mean response time: %f ms".formatted(calculateMean()));
      System.out.println("Median response time: %d ms".formatted(calculateMedian()));
      System.out.println("P99 response time: %d ms".formatted(calculatePercentile()));
      System.out.println("Min response time: %d ms".formatted(calculateMin()));
      System.out.println("Max response time: %d ms".formatted(calculateMax()));
//      System.out.println("Little\'s law suggested throughput: %f req/sec".formatted(Math.min(200, threadCount)/0.0245));
      //Generate plottable data
      Map<Integer, Integer> occurrence = new HashMap<>();
      elapsedTimes.forEach(val -> {
        occurrence.putIfAbsent(val.getCurrentTime(), 0);
        occurrence.put(val.getCurrentTime(), occurrence.get(val.getCurrentTime()) + 1);
      });
      //write to csv
      occurrence.forEach((k,v)-> {
        try {
          outputStream.write(LINE_FORMAT.formatted(k,v));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  public void addResponse(Response response) {
    if (response.isSucceed()) {
      elapsedTimes.add(new TimeStore(response.getElapsedTime(),
          Math.toIntExact(System.currentTimeMillis() - startTime) / 1000));
      successCount.getAndIncrement();
    } else {
      failedCount.getAndIncrement();
    }
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
  }

  protected int calculateMedian() {
    Median median = new Median();
    return (int) median.evaluate(
        elapsedTimes.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected int calculatePercentile() {
    Percentile percentile = new Percentile();
    percentile.setQuantile(99);
    return (int) percentile.evaluate(
        elapsedTimes.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected int calculateMin() {
    Min min = new Min();
    return (int) min.evaluate(
        elapsedTimes.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }

  protected int calculateMax() {
    Max max = new Max();
    return (int) max.evaluate(
        elapsedTimes.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }
  protected double calculateMean() {
    Mean mean = new Mean();
    return mean.evaluate(
        elapsedTimes.stream().mapToDouble(TimeStore::getElapsedTime).toArray());
  }
}

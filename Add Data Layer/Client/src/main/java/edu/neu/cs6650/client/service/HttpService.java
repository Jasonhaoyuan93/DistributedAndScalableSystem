package edu.neu.cs6650.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.client.model.FailureResponse;
import edu.neu.cs6650.client.model.MatchResponse;
import edu.neu.cs6650.client.model.PostResponse;
import edu.neu.cs6650.client.model.Response;
import edu.neu.cs6650.client.model.StatsResponse;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpService implements Closeable {

  private static ObjectMapper objectMapper = new ObjectMapper();
  private static String STATISTIC_PATH = "/TwinderFrontGate_war/twinder/stats/%d/";
  private static String MATCH_PATH = "/TwinderFrontGate_war/twinder/matches/%d/";

  private CloseableHttpClient httpClient;
  private HttpHost httpHost;

  public HttpService(int connectionCount, String hostRoute) {
    int timeout = 5;
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
        .setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();

    connectionManager.setMaxTotal(connectionCount);
    this.httpHost = new HttpHost(hostRoute, 8080);
    connectionManager.setMaxPerRoute(new HttpRoute(httpHost), connectionCount);
    this.httpClient = HttpClients.custom().setConnectionManager(connectionManager)
        .setDefaultRequestConfig(config)
        .addInterceptorLast((HttpResponseInterceptor) (httpResponse, httpContext) -> {
          if (httpResponse.getStatusLine().getStatusCode() != 404
              && httpResponse.getStatusLine().getStatusCode() / 100 >= 4) {
            System.out.println("retried");
            throw new IOException("Retry with error status %d %s.".formatted(
                httpResponse.getStatusLine().getStatusCode(),
                httpResponse.getStatusLine().getReasonPhrase()));
          }
        }).setRetryHandler((exception, executionCount, context) -> executionCount < 5).build();
  }

  public PostResponse processHttpRequest(String requestBody, String path) throws IOException {
    HttpPost postRequest = new HttpPost(path);
    postRequest.setEntity(new StringEntity(requestBody));
    postRequest.setHeader("Content-Type", "application/json");
    long startTime = System.currentTimeMillis();
    try (CloseableHttpResponse httpResponse = httpClient.execute(httpHost, postRequest)) {
      long endTime = System.currentTimeMillis();
      PostResponse postResponse = objectMapper.readValue(httpResponse.getEntity().getContent(),
          PostResponse.class);
      postResponse.setElapsedTime(endTime - startTime);
      postResponse.setSucceed(httpResponse.getStatusLine().getStatusCode() == 201);
      return postResponse;
    }
  }

  public Response obtainStatisticsById(int id) throws IOException {
    HttpGet getRequest = new HttpGet(STATISTIC_PATH.formatted(id));
    getRequest.setHeader("Content-Type", "application/json");
    long startTime = System.currentTimeMillis();
    try (CloseableHttpResponse httpResponse = httpClient.execute(httpHost, getRequest)) {
      long endTime = System.currentTimeMillis();
      if(httpResponse.getStatusLine().getStatusCode()!=200){
        FailureResponse response = objectMapper.readValue(httpResponse.getEntity().getContent(),
            FailureResponse.class);
        response.setElapsedTime(endTime - startTime);
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
      }else{
        StatsResponse response = objectMapper.readValue(httpResponse.getEntity().getContent(),
            StatsResponse.class);
        response.setElapsedTime(endTime - startTime);
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
      }
    }
  }

  public Response obtainMatchesById(int id) throws IOException {
    HttpGet getRequest = new HttpGet(MATCH_PATH.formatted(id));
    getRequest.setHeader("Content-Type", "application/json");
    long startTime = System.currentTimeMillis();
    try (CloseableHttpResponse httpResponse = httpClient.execute(httpHost, getRequest)) {
      long endTime = System.currentTimeMillis();
      if(httpResponse.getStatusLine().getStatusCode()!=200){
        FailureResponse response = objectMapper.readValue(httpResponse.getEntity().getContent(),
            FailureResponse.class);
        response.setElapsedTime(endTime - startTime);
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
      }else{
        MatchResponse response = objectMapper.readValue(
            IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8),
            MatchResponse.class);
        response.setElapsedTime(endTime - startTime);
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
      }
    }
  }

  @Override
  public void close() throws IOException {
    httpClient.close();
  }
}

package org.neu.cs6650.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.neu.cs6650.client.model.Request;
import org.neu.cs6650.client.model.Response;

public class HttpService implements Closeable {

  private static ObjectMapper objectMapper = new ObjectMapper();

  private CloseableHttpClient httpClient;
  private HttpHost httpHost;

  public HttpService(int connectionCount, String hostRoute) {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(connectionCount);
    this.httpHost = new HttpHost(hostRoute, 8080);
    connectionManager.setMaxPerRoute(new HttpRoute(httpHost), connectionCount);
    this.httpClient = HttpClients.custom().setConnectionManager(connectionManager)
        .addInterceptorLast((HttpResponseInterceptor) (httpResponse, httpContext) -> {
          if (httpResponse.getStatusLine().getStatusCode() / 100 >= 4) {
            System.out.println("retried");
            throw new IOException("Retry with error status %d %s.".formatted(
                httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase()));
          }
        })
        .setRetryHandler((exception, executionCount, context) -> executionCount < 5).build();
  }

  public Response processHttpRequest(String requestBody, String path) throws IOException {
    HttpPost postRequest = new HttpPost(path);
    postRequest.setEntity(new StringEntity(requestBody));
    postRequest.setHeader("Content-Type", "application/json");
    long startTime = System.currentTimeMillis();
    try(CloseableHttpResponse httpResponse = httpClient.execute(httpHost, postRequest)){
      long endTime = System.currentTimeMillis();
      Response response = objectMapper.readValue(IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8), Response.class);
      response.setElapsedTime(endTime - startTime);
      response.setSucceed(httpResponse.getStatusLine().getStatusCode()==201);
      return response;
    }


  }

  @Override
  public void close() throws IOException {
    httpClient.close();
  }
}

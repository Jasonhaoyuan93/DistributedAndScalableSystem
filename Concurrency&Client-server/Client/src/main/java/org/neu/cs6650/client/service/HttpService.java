package org.neu.cs6650.client.service;

import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.neu.cs6650.client.model.Response;

public class HttpService implements Closeable {

  private CloseableHttpClient httpClient;
  private HttpHost httpHost;
  private String path;

  public HttpService(int connectionCount, String hostRoute, String path) {
    this.path = path;
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(connectionCount);
    this.httpHost = new HttpHost(hostRoute, 8080);
    connectionManager.setMaxPerRoute(new HttpRoute(httpHost), connectionCount);
    this.httpClient = HttpClients.custom().setConnectionManager(connectionManager)
        .addInterceptorLast(new HttpResponseInterceptor() {
          @Override
          public void process(HttpResponse httpResponse, HttpContext httpContext)
              throws HttpException, IOException {
            if (httpResponse.getStatusLine().getStatusCode() / 100 >= 4) {
              System.out.println("retried");
              throw new IOException("Retry with error status %d %s.".formatted(
                  httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase()));
            }
          }
        })
        .setRetryHandler((exception, executionCount, context) -> executionCount < 5).build();
  }

  public Response processHttpRequest(String requestBody) throws IOException {
    HttpPost postRequest = new HttpPost(path);
    postRequest.setEntity(new StringEntity(requestBody));
    long startTime = System.currentTimeMillis();
    HttpResponse response = httpClient.execute(httpHost, postRequest);
    return new Response(response, System.currentTimeMillis() - startTime);

  }

  @Override
  public void close() throws IOException {
    httpClient.close();
  }
}

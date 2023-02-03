package org.neu.cs6650.client.model;

import org.apache.http.HttpResponse;

public class Response implements Comparable<Response>{

  private HttpResponse httpResponse;
  private long elapsedTime;

  public Response(HttpResponse httpResponse, long elapsedTime) {
    this.httpResponse = httpResponse;
    this.elapsedTime = elapsedTime;
  }

  public Response() {
    this.elapsedTime = 0;
  }

  public boolean isSuccess(){
    return httpResponse!=null && httpResponse.getStatusLine().getStatusCode()==201;
  }

  public HttpResponse getHttpResponse() {
    return httpResponse;
  }

  public void setHttpResponse(HttpResponse httpResponse) {
    this.httpResponse = httpResponse;
  }

  public long getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  @Override
  public int compareTo(Response o) {
    return Math.toIntExact(this.elapsedTime-o.elapsedTime);
  }
}

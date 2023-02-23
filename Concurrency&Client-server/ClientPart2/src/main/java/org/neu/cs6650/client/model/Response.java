package org.neu.cs6650.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Response extends Request implements Comparable<Response>{

  @JsonIgnore
  private boolean isSucceed;
  @JsonIgnore
  private long elapsedTime;
  private long startTime;
  private boolean swipeRight;

  public Response() {
  }

  public boolean isSucceed() {
    return isSucceed;
  }

  public void setSucceed(boolean succeed) {
    isSucceed = succeed;
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

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public boolean isSwipeRight() {
    return swipeRight;
  }

  public void setSwipeRight(boolean swipeRight) {
    this.swipeRight = swipeRight;
  }
}

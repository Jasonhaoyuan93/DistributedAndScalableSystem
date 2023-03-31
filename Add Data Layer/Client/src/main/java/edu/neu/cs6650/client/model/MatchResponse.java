package edu.neu.cs6650.client.model;

import java.util.List;

public class MatchResponse implements Response{

  private List<String> matchList;
  private long elapsedTime;
  private int statusCode;

  public List<String> getMatchList() {
    return matchList;
  }

  public void setMatchList(List<String> matchList) {
    this.matchList = matchList;
  }

  @Override
  public long getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
}

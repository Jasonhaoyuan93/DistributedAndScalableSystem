package edu.neu.cs6650.client.model;

public class StatsResponse implements Response{

  private int numLikes;
  private int numDislikes;
  private long elapsedTime;
  private int statusCode;

  public int getNumLikes() {
    return numLikes;
  }

  public void setNumLikes(int numLikes) {
    this.numLikes = numLikes;
  }

  public int getNumDislikes() {
    return numDislikes;
  }

  public void setNumDislikes(int numDislikes) {
    this.numDislikes = numDislikes;
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

package edu.neu.cs6650.server.model;

public class StatsResponse {

  private long numlikes;

  private long numDislikes;

  public StatsResponse(long numlikes, long numDislikes) {
    this.numlikes = numlikes;
    this.numDislikes = numDislikes;
  }

  public long getNumlikes() {
    return numlikes;
  }

  public void setNumlikes(long numlikes) {
    this.numlikes = numlikes;
  }

  public long getNumDislikes() {
    return numDislikes;
  }

  public void setNumDislikes(long numDislikes) {
    this.numDislikes = numDislikes;
  }
}

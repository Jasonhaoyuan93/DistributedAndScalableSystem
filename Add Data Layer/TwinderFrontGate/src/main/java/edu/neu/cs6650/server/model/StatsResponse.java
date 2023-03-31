package edu.neu.cs6650.server.model;

public class StatsResponse {

  private long numLikes;

  private long numDislikes;

  public StatsResponse(long numLikes, long numDislikes) {
    this.numLikes = numLikes;
    this.numDislikes = numDislikes;
  }

  public long getNumLikes() {
    return numLikes;
  }

  public void setNumLikes(long numLikes) {
    this.numLikes = numLikes;
  }

  public long getNumDislikes() {
    return numDislikes;
  }

  public void setNumDislikes(long numDislikes) {
    this.numDislikes = numDislikes;
  }
}

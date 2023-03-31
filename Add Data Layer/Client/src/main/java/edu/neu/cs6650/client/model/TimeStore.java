package edu.neu.cs6650.client.model;

public class TimeStore {

  private int elapsedTime;


  public TimeStore(long elapsedTime) {
    this.elapsedTime = Math.toIntExact(elapsedTime);
  }

  public int getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(int elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

}

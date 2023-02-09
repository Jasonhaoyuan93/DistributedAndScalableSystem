package org.neu.cs6650.client.model;

public class TimeStore {

  private int elapsedTime;

  private int currentTime;

  public TimeStore(long elapsedTime, int currentTime) {
    this.elapsedTime = Math.toIntExact(elapsedTime);
    this.currentTime = Math.toIntExact(currentTime);
  }

  public int getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(int elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public int getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(int currentTime) {
    this.currentTime = currentTime;
  }
}

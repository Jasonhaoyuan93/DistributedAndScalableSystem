package edu.neu.cs6650.client.model;

import java.util.concurrent.atomic.AtomicLong;

public class SwipeRecord {

  private int id;

  private AtomicLong swipeLeftCount;

  private AtomicLong swipeRightCount;

  public SwipeRecord(Request request) {
    this.id = Integer.parseInt(request.getSwiper());
    swipeLeftCount = new AtomicLong();
    swipeRightCount = new AtomicLong();
    increment(request);
  }

  public void increment(Request request){
    if(Integer.parseInt(request.getSwiper())!=id){
      throw new IllegalArgumentException("ID mismatch");
    }
    if (request.isSwipeRight()) {
      incrementRight();
    } else {
      incrementLeft();
    }
  }

  public int getId() {
    return id;
  }

  public AtomicLong getSwipeLeftCount() {
    return swipeLeftCount;
  }

  public AtomicLong getSwipeRightCount() {
    return swipeRightCount;
  }

  public void incrementLeft(){
    swipeLeftCount.getAndIncrement();
  }

  public void incrementRight(){
    swipeRightCount.getAndIncrement();
  }
}

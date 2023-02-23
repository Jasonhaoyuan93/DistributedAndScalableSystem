package edu.neu.cs6650.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Request {
  private String swiper;
  private String swipee;
  private String comment;
  private boolean swipeRight;
  private long startTime;

  public String getSwiper() {
    return swiper;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public String getSwipee() {
    return swipee;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isSwipeRight() {
    return swipeRight;
  }

  public void setSwipeRight(boolean swipeRight) {
    this.swipeRight = swipeRight;
  }

  public long getStartTime() {
    return startTime;
  }
}

package org.neu.cs6650.client.model;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class Request {
  private String swiper;
  private String swipee;
  private String comment;


  public Request(Random random) {
    swiper = String.valueOf(random.nextInt(1,5001));
    swipee = String.valueOf(random.nextInt(1,1000001));
    comment = RandomStringUtils.randomAlphabetic(15, 256);
  }

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
}

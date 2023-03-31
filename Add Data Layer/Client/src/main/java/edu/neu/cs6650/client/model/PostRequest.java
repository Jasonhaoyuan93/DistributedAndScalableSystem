package edu.neu.cs6650.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class PostRequest {
  private String swiper;
  private String swipee;
  private String comment;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String message;

  public PostRequest(Random random) {
    swiper = String.valueOf(random.nextInt(1,5001));
    swipee = String.valueOf(random.nextInt(1,1000001));
    comment = RandomStringUtils.randomAlphabetic(256, 257);
  }

  public PostRequest() {
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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

package org.neu.cs6650.server.model;

public class Response {
  private String message;

  public Response(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

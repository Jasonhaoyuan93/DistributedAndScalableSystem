package edu.neu.cs6650.server.model;

public class Response extends Request{
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

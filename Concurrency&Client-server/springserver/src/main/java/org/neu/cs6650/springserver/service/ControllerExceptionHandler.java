package org.neu.cs6650.springserver.service;


import javax.servlet.http.HttpServletRequest;
import org.neu.cs6650.springserver.model.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  @ResponseStatus
  public Response handleException(HttpServletRequest req, Exception e){
    return new Response(e.getMessage());
  }

}

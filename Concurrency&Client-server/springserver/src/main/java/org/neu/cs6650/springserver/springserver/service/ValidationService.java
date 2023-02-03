package org.neu.cs6650.springserver.springserver.service;

import org.apache.commons.lang3.StringUtils;
import org.neu.cs6650.springserver.springserver.model.Request;
import org.neu.cs6650.springserver.springserver.model.Response;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

  public Response swipeValidationService(String direction, Request request){

    if(!"left".equalsIgnoreCase(direction) && !"right".equalsIgnoreCase(direction)){
      return new Response("Missing Swipe direction.");
    }
    if(StringUtils.isBlank(request.getSwipee())){
      return new Response("Missing Swipee ID.");
    }
    if(StringUtils.isBlank(request.getSwiper())){
      return new Response("Missing Swiper ID.");
    }
    if(StringUtils.isBlank(request.getComment())){
      return null;
    }
    if(request.getComment().length()>256){
      return new Response("Message length exceed 256.");
    }
    return null;
  }

}

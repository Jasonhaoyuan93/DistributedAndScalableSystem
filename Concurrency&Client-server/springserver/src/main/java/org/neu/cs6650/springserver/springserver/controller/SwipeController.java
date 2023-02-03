package org.neu.cs6650.springserver.springserver.controller;


import org.neu.cs6650.springserver.springserver.model.Request;
import org.neu.cs6650.springserver.springserver.model.Response;
import org.neu.cs6650.springserver.springserver.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwipeController {

  private final ValidationService validationService;

  @Autowired
  public SwipeController(ValidationService validationService) {
    this.validationService = validationService;
  }

  @PostMapping("/twinder/swipe/{direction}")
  public ResponseEntity<Object> handleSwipe(@PathVariable String direction, @RequestBody Request request){
    Response response = validationService.swipeValidationService(direction,request);
    if(response!=null){
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}

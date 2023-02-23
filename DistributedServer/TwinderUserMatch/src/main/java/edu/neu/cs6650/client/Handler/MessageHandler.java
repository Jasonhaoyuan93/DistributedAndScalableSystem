package edu.neu.cs6650.client.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import edu.neu.cs6650.client.model.Request;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler implements DeliverCallback {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final Map<String, Set<String>> swiperMatchMap;

  public MessageHandler(Map<String, Set<String>> swiperMatchMap) {
    this.swiperMatchMap = swiperMatchMap;
  }

  @Override
  public void handle(String consumerTag, Delivery message) throws IOException {
    Request request = OBJECT_MAPPER.readValue(message.getBody(), Request.class);
    System.out.printf(" [+] Message consumed with consumerTag: %s.%n", consumerTag);
    swiperMatchMap.compute(request.getSwipee(), (k,v) -> {
      if(v==null){
        Set<String> matchSet = new HashSet<>();
        matchSet.add(request.getSwiper());
        return matchSet;
      }else{
        v.add(request.getSwiper());
        return  v;
      }
    });
  }
}

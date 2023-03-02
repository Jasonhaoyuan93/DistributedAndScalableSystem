package edu.neu.cs6650.client.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import edu.neu.cs6650.client.model.Request;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageHandler implements DeliverCallback {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final Map<String, Set<Integer>> swiperMatchMap;

  public MessageHandler(Map<String, Set<Integer>> swiperMatchMap) {
    this.swiperMatchMap = swiperMatchMap;
  }

  @Override
  public void handle(String consumerTag, Delivery message) throws IOException {
    Request request = OBJECT_MAPPER.readValue(message.getBody(), Request.class);
    swiperMatchMap.compute(request.getSwiper(), (k,v) -> {
      if(v==null){
        Set<Integer> matchSet = new HashSet<>();
        matchSet.add(Integer.parseInt(request.getSwipee()));
        return matchSet;
      }else if(v.size()<100){
        v.add(Integer.parseInt(request.getSwipee()));
        return  v;
      }
      return v;
    });
    System.out.printf(" [+] Message consumed with latency: %d ms.%n", System.currentTimeMillis()- request.getStartTime());
  }
}

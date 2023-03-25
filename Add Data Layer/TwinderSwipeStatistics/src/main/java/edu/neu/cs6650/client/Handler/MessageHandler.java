package edu.neu.cs6650.client.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import edu.neu.cs6650.client.model.Request;
import edu.neu.cs6650.client.model.SwipeRecord;

import java.io.IOException;
import java.util.Map;

public class MessageHandler implements DeliverCallback {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final Map<String, SwipeRecord> swipeRecordMap;

  public MessageHandler(Map<String, SwipeRecord> swipeRecordMap) {
    this.swipeRecordMap = swipeRecordMap;
  }

  @Override
  public void handle(String consumerTag, Delivery message) throws IOException {
    Request request = OBJECT_MAPPER.readValue(message.getBody(), Request.class);
    swipeRecordMap.compute(request.getSwiper(),(k,v)->{
      if(v==null){
        return new SwipeRecord(request);
      } else {
        v.increment(request);
        return v;
      }
    });
    System.out.printf(" [+] Message consumed with latency: %d ms.%n", System.currentTimeMillis()- request.getStartTime());
  }
}

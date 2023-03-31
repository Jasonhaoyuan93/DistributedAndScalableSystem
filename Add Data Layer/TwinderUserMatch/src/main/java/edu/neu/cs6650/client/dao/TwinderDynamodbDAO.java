package edu.neu.cs6650.client.dao;


import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class TwinderDynamodbDAO implements TwinderDAO{

  private static TwinderDynamodbDAO twinderDynamoDBDAO;
  private static final String TWINDER_MATCHES_TABLE = "twinder_matches";
  private static final String SWIPEE_ID = "swipee_id";
  private static final String SWIPER_ID = "swiper_id";
  private static DynamoDbClient dynamoDbClient;

  private static TwinderDynamodbDAO twinderDynamodbDAO;

  private TwinderDynamodbDAO() {
    Region region = Region.US_WEST_2;
    dynamoDbClient = DynamoDbClient.builder().region(region).build();
  }

  public static TwinderDynamodbDAO getInstance(){
    if(twinderDynamodbDAO == null){
      twinderDynamodbDAO = new TwinderDynamodbDAO();
    }
    return twinderDynamodbDAO;
  }

  @Override
  public void insertMatches(int swiperId, int swipeeId) throws Exception {

    Map<String,AttributeValue> itemValues = new HashMap<>();
    itemValues.put(SWIPEE_ID, AttributeValue.builder().n(String.valueOf(swipeeId)).build());
    itemValues.put(SWIPER_ID, AttributeValue.builder().n(String.valueOf(swiperId)).build());

    PutItemRequest request = PutItemRequest.builder()
        .tableName(TWINDER_MATCHES_TABLE)
        .item(itemValues)
        .build();

    try {
      dynamoDbClient.putItem(request);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }


}

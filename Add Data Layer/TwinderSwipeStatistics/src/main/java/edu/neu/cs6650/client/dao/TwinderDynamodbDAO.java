package edu.neu.cs6650.client.dao;


import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class TwinderDynamodbDAO implements TwinderDAO {

  private static TwinderDynamodbDAO twinderDynamoDBDAO;
  private static final String TWINDER_STATS_TABLE = "twinder_stats";

  private static final String USER_ID = "user_id";
  private static final String INC = ":inc";
  private static final String SAME = ":same";
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
  public void updateStatsOnUser(int userId, boolean isLike) throws Exception {

    Map<String, AttributeValue> attrKey = new HashMap<>();
    attrKey.put(USER_ID, AttributeValue.builder().n(String.valueOf(userId)).build());
    Map<String, AttributeValue> attrVal = new HashMap<>();
    attrVal.put(INC, AttributeValue.builder().n(String.valueOf(1)).build());
    attrVal.put(SAME, AttributeValue.builder().n(String.valueOf(0)).build());

    UpdateItemRequest request = UpdateItemRequest.builder()
        .tableName(TWINDER_STATS_TABLE)
        .key(attrKey)
        .updateExpression(isLike? "ADD num_like :inc, num_dislike :same":"ADD num_dislike :inc, num_like :same")
        .expressionAttributeValues(attrVal)
        .build();

    try {
      dynamoDbClient.updateItem(request);

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}

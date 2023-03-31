package edu.neu.cs6650.server.dao;


import edu.neu.cs6650.server.model.MatchResponse;
import edu.neu.cs6650.server.model.StatsResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class TwinderDynamodbDAO implements TwinderDAO {

  private static final String TWINDER_MATCHES = "twinder_matches";
  private static final String TWINDER_STATS = "twinder_stats";
  private static final String SWIPEE_ID = "swipee_id";
  private static final String SWIPER_ID = "swiper_id";
  private static final String USER_ID = "user_id";
  private static final String NUM_LIKE = "num_like";
  private static final String NUM_DISLIKE = "num_dislike";
  private static DynamoDbClient dynamoDbClient;

  public TwinderDynamodbDAO() {
    Region region = Region.US_WEST_2;
    dynamoDbClient = DynamoDbClient.builder().region(region).build();
  }

  public MatchResponse obtainMatchesBaseOnUID(int uid) {

    // Set up mapping of the partition name with the value.
    QueryResponse response = performQuery(SWIPEE_ID, uid, TWINDER_MATCHES);
    List<String> matches = new LinkedList<>();
    response.items().forEach(map -> {
      matches.add(map.get(SWIPER_ID).getValueForField("N", String.class).get());
    });
    return new MatchResponse(matches);

  }

  public StatsResponse obtainStatsBaseOnUID(int uid) {
    // Set up mapping of the partition name with the value.
    QueryResponse response = performQuery(USER_ID, uid, TWINDER_STATS);
    if (!response.items().isEmpty()) {
      int likeCount = Optional.ofNullable(response.items().get(0).get(NUM_LIKE))
          .map(v -> v.getValueForField("N", String.class).orElse("0")).map(Integer::parseInt)
          .orElse(0);
      int dislikeCount = Optional.ofNullable(response.items().get(0).get(NUM_DISLIKE))
          .map(v -> v.getValueForField("N", String.class).orElse("0")).map(Integer::parseInt)
          .orElse(0);
      return new StatsResponse(likeCount, dislikeCount);
    } else {
      return null;
    }
  }

  private QueryResponse performQuery(String userIdKey, int userId, String twinderStats) {
    Map<String, AttributeValue> attrValues = new HashMap<>();

    attrValues.put(":" + userIdKey, AttributeValue.builder().n(String.valueOf(userId)).build());

    QueryRequest queryReq = QueryRequest.builder().tableName(twinderStats)
        .keyConditionExpression(userIdKey + " = :" + userIdKey)
        .expressionAttributeValues(attrValues).build();

    return dynamoDbClient.query(queryReq);
  }


}

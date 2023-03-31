package edu.neu.cs6650.server.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.neu.cs6650.server.model.MatchResponse;
import edu.neu.cs6650.server.model.StatsResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TwinderMysqlDAO implements TwinderDAO{
  private static final String STATS_QUERY = "select num_like,num_dislike from twinder_stats where user_id = ?";
  private static final String MATCH_QUERY = "select swiper_id from twinder_matches where swipee_id = ?";
  private static HikariDataSource dataSource;

  public TwinderMysqlDAO() {
    String url = System.getProperty("jdbc.url","jdbc:mysql://localhost:3306/Twinder");
    String username = System.getProperty("jdbc.username","root");
    String password = System.getProperty("jdbc.password","root");
    HikariConfig dbConfig = new HikariConfig();
    dbConfig.setJdbcUrl(url);
    dbConfig.setUsername(username);
    dbConfig.setPassword(password);
    dbConfig.addDataSourceProperty("cachePrepStmts", "true");
    dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    dbConfig.addDataSourceProperty("readOnly", "true");
    dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource = new HikariDataSource(dbConfig);
  }

  @Override
  public MatchResponse obtainMatchesBaseOnUID(int uid) throws SQLException {
    try(Connection conn = dataSource.getConnection();
        PreparedStatement queryMatches = conn.prepareStatement(MATCH_QUERY)){
        queryMatches.setLong(1,uid);
        ResultSet resultSet = queryMatches.executeQuery();
        List<Integer> matchList = new LinkedList<>();
        while(resultSet.next()){
          matchList.add(resultSet.getInt(1));
        }
        return new MatchResponse(matchList.stream().map(String::valueOf).toList());
    }
  }

  @Override
  public StatsResponse obtainStatsBaseOnUID(int uid) throws SQLException {
    try(Connection conn = dataSource.getConnection();
        PreparedStatement queryMatches = conn.prepareStatement(STATS_QUERY)){
      queryMatches.setLong(1,uid);
      ResultSet resultSet = queryMatches.executeQuery();
      StatsResponse statsResponse = null;
      while(resultSet.next()){
        statsResponse = new StatsResponse(resultSet.getLong(1),resultSet.getLong(2));
      }
      return statsResponse;
    }
  }
}

package edu.neu.cs6650.client.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TwinderDAO {
  private static final String LIKE_QUERY = "insert into twinder.twinder_stats(user_id,num_like,num_dislike) values (?,1,0) on duplicate key update num_like = num_like + 1";
  private static final String DISLIKE_QUERY = "insert into twinder.twinder_stats(user_id,num_like,num_dislike) values (?,0,1) on duplicate key update num_dislike = num_dislike + 1;";

  private static TwinderDAO cachedTwinderDAO;
  private static HikariDataSource dataSource;

  private TwinderDAO() {
    String url = "jdbc linkr";
    String username = "root";
    String password = "pass";
    HikariConfig dbConfig = new HikariConfig();
    dbConfig.setJdbcUrl(url);
    dbConfig.setUsername(username);
    dbConfig.setPassword(password);
    dbConfig.addDataSourceProperty("cachePrepStmts", "true");
    dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dbConfig.setAutoCommit(true);
    dataSource = new HikariDataSource(dbConfig);
  }

  public static TwinderDAO getInstance(){
    if(cachedTwinderDAO == null){
      cachedTwinderDAO = new TwinderDAO();
    }
    return cachedTwinderDAO;
  }

  public void updateStatsOnUser(int uid, boolean isLike) throws SQLException {
    try(Connection conn = dataSource.getConnection();
        PreparedStatement queryMatches = conn.prepareStatement(isLike?LIKE_QUERY:DISLIKE_QUERY)){
      queryMatches.setLong(1,uid);
      queryMatches.executeUpdate();
    }
  }
}

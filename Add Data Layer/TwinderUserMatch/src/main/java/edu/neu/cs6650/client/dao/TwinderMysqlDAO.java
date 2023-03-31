package edu.neu.cs6650.client.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TwinderMysqlDAO implements TwinderDAO{

  private static final String MATCH_QUERY = "insert into twinder.twinder_matches(swiper_id,swipee_id) values (?,?) on duplicate key update swipe_date = default";

  private static TwinderMysqlDAO cachedTwinderMysqlDAO;
  private static HikariDataSource dataSource;

  private TwinderMysqlDAO() {
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
    dbConfig.addDataSourceProperty("maximumPoolSize", "600");
    dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dbConfig.setAutoCommit(true);
    dataSource = new HikariDataSource(dbConfig);
  }

  public static TwinderMysqlDAO getInstance(){
    if(cachedTwinderMysqlDAO == null){
      cachedTwinderMysqlDAO = new TwinderMysqlDAO();
    }
    return cachedTwinderMysqlDAO;
  }

  public void insertMatches(int swiperId, int swipeeId) throws SQLException {
    try(Connection conn = dataSource.getConnection();
        PreparedStatement queryMatches = conn.prepareStatement(MATCH_QUERY)){
        queryMatches.setLong(1,swiperId);
        queryMatches.setLong(2,swipeeId);
        queryMatches.executeUpdate();
    }
  }

}

package edu.neu.cs6650.client.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TwinderDAO {

  private static final String MATCH_QUERY = "insert into twinder.twinder_matches(swiper_id,swipee_id) values (?,?) ";

  private static TwinderDAO cachedTwinderDAO;
  private static HikariConfig dbConfig ;
  private static HikariDataSource dataSource;

  private TwinderDAO() {
    String url = "jdbc:mysql://localhost:3306/Twinder";
    String username = "root";
//    String password = "root";
    dbConfig = new HikariConfig();
    dbConfig.setJdbcUrl(url);
    dbConfig.setUsername(username);
//    dbConfig.setPassword(password);
    dbConfig.addDataSourceProperty("cachePrepStmts", "true");
    dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    dbConfig.addDataSourceProperty("readOnly", "true");
    dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dbConfig.setSchema("Twinder");
    dataSource = new HikariDataSource(dbConfig);
  }

  public static TwinderDAO getInstance(){
    if(cachedTwinderDAO == null){
      cachedTwinderDAO = new TwinderDAO();
    }
    return cachedTwinderDAO;
  }

  public void insertMatches(int swiper_id, int swipee_id) throws SQLException {
    try(Connection conn = dataSource.getConnection();
        PreparedStatement queryMatches = conn.prepareStatement(MATCH_QUERY)){
        queryMatches.setLong(1,swiper_id);
        queryMatches.setLong(2,swipee_id);
        queryMatches.executeQuery();
    }
  }
}

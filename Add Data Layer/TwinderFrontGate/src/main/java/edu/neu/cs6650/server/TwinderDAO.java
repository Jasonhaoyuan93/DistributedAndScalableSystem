package edu.neu.cs6650.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.neu.cs6650.server.model.MatchResponse;
import edu.neu.cs6650.server.model.StatsResponse;
import java.util.ArrayList;

public class TwinderDAO {
  private static TwinderDAO cachedTwinderDAO;
  private static HikariConfig dbConfig ;
  private static HikariDataSource dataSource;

  private TwinderDAO() {
    String url = "jdbc:mysql://localhost:3306/?user=root";
    String username = "root";
    String password = "root";
    dbConfig = new HikariConfig();
    dbConfig.setJdbcUrl(url);
    dbConfig.setUsername(username);
    dbConfig.setPassword(password);
    dbConfig.addDataSourceProperty("cachePrepStmts", "true");
    dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    dbConfig.addDataSourceProperty("readOnly", "true");    
    dataSource = new HikariDataSource(dbConfig);
  }

  public static TwinderDAO getInstance(){
    if(cachedTwinderDAO == null){
      cachedTwinderDAO = new TwinderDAO();
    }
    return cachedTwinderDAO;
  }

  public MatchResponse obtainMatchesBaseOnUID(int uid){
    return new MatchResponse(new ArrayList<>());
  }

  public StatsResponse obtainStatsBaseOnUID(int uid){
    return new StatsResponse(12,12);
  }
}

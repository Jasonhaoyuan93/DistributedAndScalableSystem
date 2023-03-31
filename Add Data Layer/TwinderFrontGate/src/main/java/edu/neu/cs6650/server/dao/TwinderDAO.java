package edu.neu.cs6650.server.dao;

import edu.neu.cs6650.server.model.MatchResponse;
import edu.neu.cs6650.server.model.StatsResponse;

public interface TwinderDAO {

  public MatchResponse obtainMatchesBaseOnUID(int uid) throws Exception;

  public StatsResponse obtainStatsBaseOnUID(int uid) throws Exception;
}

package edu.neu.cs6650.client.dao;

public interface TwinderDAO {

  void updateStatsOnUser(int userId, boolean isLike) throws Exception;

}

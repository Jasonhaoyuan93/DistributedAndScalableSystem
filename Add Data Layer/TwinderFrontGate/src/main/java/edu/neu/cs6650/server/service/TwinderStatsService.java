package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.server.dao.TwinderDAO;
import edu.neu.cs6650.server.model.MessageResponse;
import edu.neu.cs6650.server.model.StatsResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class TwinderStatsService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TwinderDAO twinderDAO;

  public TwinderStatsService() {
    twinderDAO = TwinderDAO.getInstance();
  }

  public void obtainStatsWithUID(int uid, HttpServletResponse httpServletResponse)
      throws IOException {
    StatsResponse statsResponse = null;
    try {
      statsResponse = twinderDAO.obtainStatsBaseOnUID(uid);
      if(statsResponse==null){
        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(new MessageResponse("User not found")));
      }else{
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(statsResponse));
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}

package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.server.TwinderDAO;
import edu.neu.cs6650.server.model.StatsResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class TwinderStatsService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TwinderDAO twinderDAO;

  public TwinderStatsService() {
    twinderDAO = TwinderDAO.getInstance();
  }

  public void obtainStatsWithUID(int uid, HttpServletResponse httpServletResponse)
      throws IOException {
    StatsResponse statsResponse = twinderDAO.obtainStatsBaseOnUID(uid);
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    httpServletResponse.getWriter().write(objectMapper.writeValueAsString(statsResponse));
  }
}

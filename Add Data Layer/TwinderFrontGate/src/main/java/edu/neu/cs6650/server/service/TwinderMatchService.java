package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.server.TwinderDAO;
import edu.neu.cs6650.server.model.MatchResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class TwinderMatchService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TwinderDAO twinderDAO;
  public TwinderMatchService() {
    twinderDAO = TwinderDAO.getInstance();
  }

  public void findMatches(int uid, HttpServletResponse httpServletResponse) throws IOException {
    MatchResponse matches = twinderDAO.obtainMatchesBaseOnUID(uid);
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    httpServletResponse.getWriter().write(objectMapper.writeValueAsString(matches));
  }
}

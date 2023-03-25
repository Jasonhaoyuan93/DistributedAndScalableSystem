package edu.neu.cs6650.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.server.dao.TwinderDAO;
import edu.neu.cs6650.server.model.MatchResponse;
import edu.neu.cs6650.server.model.MessageResponse;
import edu.neu.cs6650.server.model.Response;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;

public class TwinderMatchService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TwinderDAO twinderDAO;
  public TwinderMatchService() {
    twinderDAO = TwinderDAO.getInstance();
  }

  public void findMatches(int uid, HttpServletResponse httpServletResponse) throws IOException {
    MatchResponse matches = null;
    try {
      matches = twinderDAO.obtainMatchesBaseOnUID(uid);
      if(matches.getMatchList().isEmpty()){
        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(new MessageResponse("User not found")));
      }else{
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(matches));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

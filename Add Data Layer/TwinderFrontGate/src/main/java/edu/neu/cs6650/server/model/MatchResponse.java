package edu.neu.cs6650.server.model;

import java.util.List;

public class MatchResponse {

  private List<String> matchList;

  public MatchResponse(List<String> matchList) {
    this.matchList = matchList;
  }

  public List<String> getMatchList() {
    return matchList;
  }

  public void setMatchList(List<String> matchList) {
    this.matchList = matchList;
  }
}

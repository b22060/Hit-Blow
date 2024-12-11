package kmt.hit_blow.model;

import java.util.ArrayList;

public class SSEMatch {
  ArrayList<MatchInfo> matchInfo;
  int insertedid;
  String message;
  int goalflag;

  public SSEMatch(ArrayList<MatchInfo> matchInfo, int insertedid, String message, int goalflag) {
    this.matchInfo = matchInfo;
    this.insertedid = insertedid;
    this.message = message;
    this.goalflag = goalflag;
  }

  public ArrayList<MatchInfo> getMatchInfo() {
    return matchInfo;
  }

  public void setMatchInfo(ArrayList<MatchInfo> matchInfo) {
    this.matchInfo = matchInfo;
  }

  public int getInsertedid() {
    return insertedid;
  }

  public void setInsertedid(int insertedid) {
    this.insertedid = insertedid;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getGoalflag() {
    return goalflag;
  }

  public void setGoalflag(int goalflag) {
    this.goalflag = goalflag;
  }

}

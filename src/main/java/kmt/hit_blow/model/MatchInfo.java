package kmt.hit_blow.model;

public class MatchInfo {
  int matchinfoid;
  int matchid;
  int userid;
  String guess;
  int hit;
  int blow;

  public int getMatchinfoid() {
    return matchinfoid;
  }

  public void setMatchinfoid(int matchinfoid) {
    this.matchinfoid = matchinfoid;
  }

  public int getMatchid() {
    return matchid;
  }

  public void setMatchid(int matchid) {
    this.matchid = matchid;
  }

  public int getUserid() {
    return userid;
  }

  public void setUserid(int userid) {
    this.userid = userid;
  }

  public String getGuess() {
    return guess;
  }

  public void setGuess(String guess) {
    this.guess = guess;
  }

  public int getHit() {
    return hit;
  }

  public void setHit(int hit) {
    this.hit = hit;
  }

  public int getBlow() {
    return blow;
  }

  public void setBlow(int blow) {
    this.blow = blow;
  }

}

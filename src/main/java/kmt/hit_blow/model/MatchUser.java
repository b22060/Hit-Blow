package kmt.hit_blow.model;

public class MatchUser {
  Match match;
  String username1;
  String username2;

  public MatchUser(Match match, String username1, String username2) {
    this.match = match;
    this.username1 = username1;
    this.username2 = username2;
  }

  public String getUsername1() {
    return username1;
  }

  public void setUsername1(String username1) {
    this.username1 = username1;
  }

  public String getUsername2() {
    return username2;
  }

  public void setUsername2(String username2) {
    this.username2 = username2;
  }

  public Match getMatch() {
    return match;
  }

  public void setMatch(Match match) {
    this.match = match;
  }

}

package kmt.hit_blow.model;

public class Match {
  int matchid;
  int userid1; //自分のid
  int userid2; //相手のid
  String usernum1; //自分の答え
  String usernum2; //相手の答え
  String judge; //勝敗
  boolean isActive;//試合状態

  public Match(int userid1, int userid2, String myanswer, String rivalanswer, String judge, boolean isActive) {//試合前のコンストラクタ
    this.userid1 = userid1;
    this.userid2 = userid2;
    this.usernum1 = myanswer;
    this.usernum2 = rivalanswer;
    this.judge = judge;
    this.isActive = isActive;
  }

  public Match(int matchid, int userid1, int userid2, String myanswer, String rivalanswer, String judge,boolean isActive) {// 試合後のコンストラクタ
    this.matchid = matchid;
    this.userid1 = userid1;
    this.userid2 = userid2;
    this.usernum1 = myanswer;
    this.usernum2 = rivalanswer;
    this.judge = judge;
    this.isActive = isActive;
  }

  public int getMatchid() {
    return matchid;
  }

  public void setMatchid(int matchid) {
    this.matchid = matchid;
  }

  public int getUserid1() {
    return userid1;
  }

  public void setUserid1(int userid1) {
    this.userid1 = userid1;
  }

  public int getUserid2() {
    return userid2;
  }

  public void setUserid2(int userid2) {
    this.userid2 = userid2;
  }

  public String getUsernum1() {
    return usernum1;
  }

  public void setUsernum1(String usernum1) {
    this.usernum1 = usernum1;
  }

  public String getUsernum2() {
    return usernum2;
  }

  public void setUsernum2(String usernum2) {
    this.usernum2 = usernum2;
  }

  public String getJudge() {
    return judge;
  }

  public void setJudge(String judge) {
    this.judge = judge;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
}

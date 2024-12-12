package kmt.hit_blow.model;

import java.util.ArrayList;

public class SSEMatch {// SSE通信でmatch2PC.htmlに送るために使うmodel
  ArrayList<MatchInfo> matchInfo;// SSE通信で送るmatchInfo配列を格納
  String message;// SSE通信で更新されたメッセージを格納
  int goalflag;// SSE通信で対戦相手がHit4であったかを格納する goalflag=1でHit4であったと分かる

  public SSEMatch(ArrayList<MatchInfo> matchInfo, String message, int goalflag) {
    this.matchInfo = matchInfo;

    this.message = message;
    this.goalflag = goalflag;
  }

  public ArrayList<MatchInfo> getMatchInfo() {
    return matchInfo;
  }

  public void setMatchInfo(ArrayList<MatchInfo> matchInfo) {
    this.matchInfo = matchInfo;
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

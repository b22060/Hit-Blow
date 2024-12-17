package kmt.hit_blow.model;

public class GameData {
  int matchid;// 該当するmatchidを格納 user1とuser2間では同じ（matchテーブル内のuser1、user2)
  int id;// Userテーブルのidを格納
  String name;// Userテーブルのnameを格納
  String secret;// 秘密の数字を格納
  int itemflag;// アイテムフラグを格納

  public GameData(int matchid, int id, String name, String secret) {
    this.matchid = matchid;
    this.id = id;
    this.name = name;
    this.secret = secret;
    this.itemflag = 1;
  }

  public int getMatchid() {
    return matchid;
  }

  public void setMatchid(int matchid) {
    this.matchid = matchid;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public int getItemflag() {
    return itemflag;
  }

  public void setItemflag(int itemflag) {
    this.itemflag = itemflag;
  }

}

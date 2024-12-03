package kmt.hit_blow.model;

public class User {
  int userid;
  String name;

  public User(int userid, String name) {
    this.userid = userid;
    this.name = name;
  }

  public int getUserid() {
    return userid;
  }

  public void setUserid(int userid) {
    this.userid = userid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

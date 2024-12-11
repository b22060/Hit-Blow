package kmt.hit_blow.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HitAndBlow {

  // コンストラクタ
  public HitAndBlow() {
  }

  public boolean numFormat(int[] input) {// 入力が重複しているか確認する
    // charc型
    // right than string
    HashSet<String> set = new HashSet<String>();
    for (int i = 0; i < input.length; i++) {
      set.add(String.valueOf(input[i]));
    }
    return set.size() == 4;
  }

  public int[] chackHit_Blow(int[] input, int[] answer) {// HitとBlowの数を数える
    int Hit = 0;
    int Blow = 0;
    for (int i = 0; i < input.length; i++) {
      if (input[i] == answer[i]) {
        Hit++;
      } else if (contains(answer, input[i])) {
        Blow++;
      }
    }
    return new int[] { Hit, Blow };
  }

  public static boolean contains(int[] answers, int guess) { // Blowの判定を行う
    for (int answer : answers) {
      if (answer == guess)
        return true;
    }
    return false;
  }

  public String translateString(int[] in) {// int配列の値を文字列に変換する
    StringBuilder sb = new StringBuilder();
    for (int num : in) {
      sb.append(num);
    }
    return sb.toString(); //
  }

  public int[] generateNumber() {
    List<Integer> numbers = new ArrayList<>(); // ランダムな値を生成
    int[] generatenumbers = new int[4];
    for (int i = 0; i <= 9; i++) {
      numbers.add(i);
    }

    Collections.shuffle(numbers);// 数字をシャッフルする

    for (int i = 0; i < 4; i++) {// 先頭4桁を格納する
      generatenumbers[i] = numbers.get(i);
    }
    return generatenumbers;
  }

  public String generateWaitMessage(GameData user1, GameData user2, int turn) {
    String message = "";
    switch (turn % 2) {
      case 1:
        message = user1.getName() + "の数字入力を待っています。";
        break;
      case 2:
        message = user2.getName() + "の数字入力を待っています。";
        break;

    }
    return message;
  }

  public String getMySecret(GameData user1, GameData user2, String myname) {
    String mysecret = "";
    if (myname.equals(user1.getName())) {
      mysecret = user1.getSecret();
    } else {
      mysecret = user2.getSecret();
    }
    return mysecret;
  }

  public String getRivalName(GameData user1, GameData user2, String myname) {
    String rivalname = "";
    if (myname.equals(user1.getName())) {
      rivalname = user2.getName();
    } else {
      rivalname = user1.getName();
    }
    return rivalname;
  }

  public boolean checkTypist(GameData user1, GameData user2, String myname, int turn) {
    boolean check = false;
    if (myname.equals(user1.getName())) {
      if (turn % 2 == 1) {// user1が入力するべきとき
        check = true;
      }
    } else {
      if (turn % 2 == 0) {// user2が入力するべきとき
        check = true;
      }
    }
    return check;
  }
}

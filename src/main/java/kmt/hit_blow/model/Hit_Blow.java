package kmt.hit_blow.model;

import java.util.HashSet;

public class Hit_Blow {

  // コンストラクタ
  public Hit_Blow() {
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

  public int[] chackHit(int[] input, int[] answer) {// HitとBlowの数を数える
    int Hit = 0;
    int Blow = 0;
    for (int i = 0; i < input.length; i++) {
      if (input[i] == answer[i]) {
        Hit++;
      }else if(contains(answer, input[i])){
        Blow++;
      }
    }
    return new int[]{Hit,Blow};
  }

  public static boolean contains(int[] answers, int guess) { //Blowの判定を行う
    for (int answer : answers) {
      if (answer == guess)
        return true;
    }
    return false;
  }
}

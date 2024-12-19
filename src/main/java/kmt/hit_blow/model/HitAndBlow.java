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
      case 0:
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

  public int getRivalId(GameData user1, GameData user2, String myname) {
    int rivalid = -1;
    if (myname.equals(user1.getName())) {
      rivalid = user2.getId();
    } else {
      rivalid = user1.getId();
    }
    return rivalid;
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

  public String getRivalSecret(GameData user1, GameData user2, String myname) {
    String rivalsecret = "";
    if (myname.equals(user1.getName())) {
      rivalsecret = user2.getSecret();
    } else {
      rivalsecret = user1.getSecret();
    }
    return rivalsecret;
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

  public int[] translateInt(String in) {// int配列の値を文字列に変換する
    int[] inarray = in.chars() // 各文字をストリームに変換
        .map(Character::getNumericValue) // 数値として解釈
        .toArray();
    return inarray;
  }

  public int getMyItemFlag(GameData user1, GameData user2, String myname) {
    int item = 0;
    if (myname.equals(user1.getName())) {
      item = user1.getItemflag();
    } else {
      item = user2.getItemflag();
    }
    return item;
  }

  public void setMyItemFlag(GameData user1, GameData user2, String myname, int num) {

    if (myname.equals(user1.getName())) {
      user1.setItemflag(num);
    } else {
      user2.setItemflag(num);
    }

  }

  // 入力が有効か確認
  public boolean isValidInput(String input) {
    if (input.length() != 4)
      return false;
    boolean[] digits = new boolean[10];
    for (char c : input.toCharArray()) {
      if (!Character.isDigit(c))
        return false;
      int digit = c - '0';
      if (digits[digit])
        return false; // 重複チェック
      digits[digit] = true;
    }
    return true;
  }

  // すべての候補を生成
  public List<String> generateAllCandidates() {
    List<String> candidates = new ArrayList<>();
    for (int i = 0; i <= 9999; i++) {
      String number = String.format("%04d", i);
      if (isValidInput(number)) {
        candidates.add(number);
      }
    }
    return candidates;
  }

  // 候補を絞り込む
  public List<String> filterCandidates(List<String> candidates, String guess, int hits, int blows) {
    List<String> filtered = new ArrayList<>();
    for (String candidate : candidates) {
      if (evaluate(guess, candidate, hits, blows)) {
        filtered.add(candidate);
      }
    }
    return filtered;
  }

  // ヒットとブローを評価
  public boolean evaluate(String guess, String candidate, int hits, int blows) {
    int actualHits = 0;
    int actualBlows = 0;

    boolean[] usedInGuess = new boolean[4];
    boolean[] usedInCandidate = new boolean[4];

    // ヒットをチェック
    for (int i = 0; i < 4; i++) {
      if (guess.charAt(i) == candidate.charAt(i)) {
        actualHits++;
        usedInGuess[i] = true;
        usedInCandidate[i] = true;
      }
    }

    // ブローをチェック
    for (int i = 0; i < 4; i++) {
      if (usedInGuess[i])
        continue;
      for (int j = 0; j < 4; j++) {
        if (usedInCandidate[j])
          continue;
        if (guess.charAt(i) == candidate.charAt(j)) {
          actualBlows++;
          usedInCandidate[j] = true;
          break;
        }
      }
    }

    return actualHits == hits && actualBlows == blows;
  }

  public int[] changeint(String guess) {
    // 1. Stringを1文字ずつ分割してchar配列に変換
    char[] charArray = guess.toCharArray();

    // 2. char配列をint配列に変換
    int[] intArray = new int[charArray.length];
    for (int i = 0; i < charArray.length; i++) {
      intArray[i] = Character.getNumericValue(charArray[i]);
    }
    return intArray;
  }
}

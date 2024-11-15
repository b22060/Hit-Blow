package kmt.hit_blow.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.ArrayList;

import kmt.hit_blow.model.HitAndBlow;
import kmt.hit_blow.model.UserMapper;
import kmt.hit_blow.model.User;
import kmt.hit_blow.model.MatchMapper;
import kmt.hit_blow.model.Match;
import kmt.hit_blow.model.MatchInfo;
import kmt.hit_blow.model.MatchInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

@Controller
public class HitAndBlowController {

  int flag = 0;
  int[] answer = new int[4];// 4つの場合
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private MatchMapper matchMapper;
  @Autowired
  private MatchInfoMapper matchInfoMapper;

  @GetMapping("/hit-blow") // hit-blow.htmlに遷移する
  public String hit_blow(ModelMap model) {
    // 表示に必要なデータをMapperで格納する
    ArrayList<User> users = userMapper.selectAllByUsers();
    ArrayList<Match> matches = matchMapper.selectAllBymatches();

    // 表示に必要なデータをmodelに渡す
    model.addAttribute("users", users);
    model.addAttribute("matches", matches);
    return "hitandblow.html";
  }

  @GetMapping("/sample") // 練習で使用したため関係なし
  public String sample(ModelMap model) {
    String a = "成功";
    model.addAttribute("success", a);
    return "hitandblow.html";
  }

  @GetMapping("/history") // historyに遷移する
  public String history(@RequestParam("matchid") int matchid, ModelMap model) {
    Match match = matchMapper.selectMatchById(matchid);
    ArrayList<MatchInfo> matchInfo = matchInfoMapper.selectByMatchId(matchid);

    model.addAttribute("match", match);
    model.addAttribute("matchInfo", matchInfo);
    return "history.html";
  }

  @GetMapping("/match") // matchに遷移する
  public String match(@RequestParam Integer userid, ModelMap model) {
    String name = userMapper.selectNameByUsers(userid);// 対戦相手の名前を取得する変数
    model.addAttribute("name", name);// Thymeleafで値をHTMLに渡す
    return "match.html";
  }

  @PostMapping("/play") // 実際のゲームを行う
  public String play(@RequestParam Integer line1, @RequestParam Integer line2, @RequestParam Integer line3,
      @RequestParam Integer line4, ModelMap model) {
    int[] in = { line1, line2, line3, line4 }; // 入力を配列に格納する
    int Hit = 0; // Hitを数える変数
    int Blow = 0; // Blowを数える変数
    int[] Hit_Blow; // HitとBlowの値を格納する配列
    int goakflag = 0; // 正解かどうかの判定
    int Formatcheak = 1; // 入力が正常か確認する変数
    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す

    if (cheak.numFormat(in) != true) { // 数値の重複があるかの確認
      Formatcheak = 2;
    }

    if (this.flag == 0) { // 初回はここに入る
      List<Integer> numbers = new ArrayList<>(); // ランダムな値を生成
      for (int i = 0; i <= 9; i++) {
        numbers.add(i);
      }
      Collections.shuffle(numbers);
      for (int i = 0; i < 4; i++) {
        this.answer[i] = numbers.get(i);
      }
      this.flag = 1; // 生成は1回のみだから
    }

    Hit_Blow = cheak.chackHit(in, this.answer);// HitとBlowを確認する
    Hit = Hit_Blow[0];
    Blow = Hit_Blow[1];

    if (Hit == 4) { // Hitが4だと正解にする
      goakflag = 1;
      this.flag = 0;
    }

    model.addAttribute("Hit", Hit); // Thymeleafで値をHTMLに渡す
    model.addAttribute("Blow", Blow);
    model.addAttribute("answers", answer);
    model.addAttribute("goalflag", goakflag);
    model.addAttribute("Formatcheak", Formatcheak);

    return "hitandblow.html";
  }
}

package kmt.hit_blow.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.ArrayList;

import kmt.hit_blow.model.HitAndBlow;
import kmt.hit_blow.model.UserMapper;
import kmt.hit_blow.service.AsyncHitAndBlow;
import kmt.hit_blow.model.User;
import kmt.hit_blow.model.MatchMapper;
import kmt.hit_blow.model.Match;
import kmt.hit_blow.model.MatchInfo;
import kmt.hit_blow.model.MatchInfoMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

@Controller
public class HitAndBlowController {

  private final Logger logger = LoggerFactory.getLogger(HitAndBlowController.class);

  @Autowired
  private AsyncHitAndBlow HaB;

  int flag = 0;
  int[] playeranswer = new int[4];// 自分の回答
  int[] rivalanswer = new int[4];// 相手の回答
  String Myanswers;
  String Rivalanswers;
  int battleid = 0; // 対戦相手のidを格納する
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private MatchMapper matchMapper;
  @Autowired
  private MatchInfoMapper matchInfoMapper;

  @GetMapping("/match/step1")
  public SseEmitter step1(@AuthenticationPrincipal UserDetails user) {
    // infoレベルでログを出力する
    String role = "USER";
    logger.info("pushFruit");
    final SseEmitter sseEmitter = new SseEmitter();
    this.HaB.count(sseEmitter,role);
    return sseEmitter;
  }

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
  public String history(@RequestParam("matchid") int matchid, ModelMap model, Principal prin) {
    Match match = matchMapper.selectMatchById(matchid);
    ArrayList<MatchInfo> matchInfo = matchInfoMapper.selectByMatchId(matchid);
    String loginUser = prin.getName(); // ログイン名を取得
    int loginUser_id = userMapper.selectIdByName(loginUser);// 自分のID取得

    model.addAttribute("match", match);
    model.addAttribute("matchInfo", matchInfo);
    model.addAttribute("battleid", battleid);
    model.addAttribute("loginid", loginUser_id);
    return "history.html";
  }

  @GetMapping("/match") // 対戦相手を決定する
  public String match(@RequestParam Integer userid, ModelMap model) {
    String name = userMapper.selectNameByUsers(userid);// 対戦相手の名前を取得する変数
    model.addAttribute("name", name);// Thymeleafで値をHTMLに渡す
    this.battleid = userid;// ここは一度しか経由しないから
    return "match.html";
  }

  @PostMapping("/play") // ここで対戦の処理をする
  public String play(@RequestParam Integer line1, @RequestParam Integer line2, @RequestParam Integer line3,
      @RequestParam Integer line4, ModelMap model, Principal prin) {
    int[] in = { line1, line2, line3, line4 }; // 入力を配列に格納する
    int myHit = 0; // 自分のHitを数える変数
    int myBlow = 0; // 自分のBlowを数える変数
    int rivalHit = 0; // 相手のHitを数える変数
    int rivalBlow = 0; // 相手のBlowを数える変数
    int[] Hit_Blow; // HitとBlowの値を格納する配列
    int goakflag = 0; // 正解かどうかの判定
    int Formatcheak = 1; // 入力が正常か確認する変数
    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す

    String loginUser = prin.getName(); // ログイン名を取得
    int loginUser_id = userMapper.selectIdByName(loginUser);// 自分のID取得

    if (this.flag == 0) { // 初回はここに入る
      List<Integer> numbers = new ArrayList<>(); // ランダムな値を生成

      for (int i = 0; i <= 9; i++) {
        numbers.add(i);
      }

      Collections.shuffle(numbers);

      for (int i = 0; i < 4; i++) {
        this.playeranswer[i] = numbers.get(i);
      }

      StringBuilder sb = new StringBuilder();

      for (int num : playeranswer) {
        sb.append(num);
      }

      String myanswer = sb.toString(); // ここで自分の答えを4桁の文字列にする

      // ここからは相手の答えを生成する
      Collections.shuffle(numbers);

      for (int i = 0; i < 4; i++) {
        this.rivalanswer[i] = numbers.get(i);
      }
      StringBuilder sa = new StringBuilder();
      for (int num : rivalanswer) {
        sa.append(num);
      }

      String rivalanswer = sa.toString(); // ここで相手の答えを4桁の文字列にする
      this.Myanswers = myanswer;
      this.Rivalanswers = rivalanswer;
      Match match = new Match(loginUser_id, battleid, myanswer, rivalanswer, "");// 自分のid,相手のid,自分の答え,相手の答えを格納
      matchMapper.insertMatch(match);// 1試合追加(勝敗は不明)
      this.flag = 1; // 生成は1回のみだから
    }

    if (cheak.numFormat(in) != true) { // 数値の重複があるかの確認
      Formatcheak = 2;
    }

    String name = userMapper.selectNameByUsers(battleid);// 対戦相手の名前を取得する変数
    model.addAttribute("name", name);// Thymeleafで値をHTMLに渡す

    int matchid = matchMapper.selectMatchIdByuserId(loginUser_id, battleid);

    Hit_Blow = cheak.chackHit_Blow(in, this.playeranswer);// HitとBlowを確認する
    myHit = Hit_Blow[0];
    myBlow = Hit_Blow[1];
    StringBuilder sb = new StringBuilder();
    for (int num : in) {
      sb.append(num);
    }
    String myguess = sb.toString(); // ここで自分の予想を4桁の文字列にする
    MatchInfo mymatchInfo = new MatchInfo(matchid, loginUser_id, myguess, myHit, myBlow); // 情報を格納する
    matchInfoMapper.insertMatchInfo(mymatchInfo);

    if (myHit == 4) { // Hitが4だと正解にする
      goakflag = 1;
      this.flag = 0;
      Match match = new Match(matchid, loginUser_id, battleid, this.Myanswers, this.Rivalanswers, "勝利");// 勝敗を更新
      matchMapper.updateById(match);
    } else {
      // ここでcpuの手を決める
      List<Integer> numbers = new ArrayList<>(); // ランダムな値を生成
      int[] rivalguess = new int[4];
      for (int i = 0; i <= 9; i++) {
        numbers.add(i);
      }
      Collections.shuffle(numbers);
      for (int i = 0; i < 4; i++) {
        rivalguess[i] = numbers.get(i);
      }

      Hit_Blow = cheak.chackHit_Blow(rivalguess, this.rivalanswer);
      rivalHit = Hit_Blow[0];
      rivalBlow = Hit_Blow[1];
      StringBuilder sa = new StringBuilder();
      for (int num : rivalguess) {
        sa.append(num);
      }
      String rivalguesshand = sa.toString(); // ここで相手の予想を4桁の文字列にする
      MatchInfo rivalmatchInfo = new MatchInfo(matchid, battleid, rivalguesshand, rivalHit, rivalBlow); // 情報を格納する
      matchInfoMapper.insertMatchInfo(rivalmatchInfo);
    }

    if (rivalHit == 4) {
      goakflag = 1;
      this.flag = 0;
      Match match = new Match(matchid, loginUser_id, battleid, this.Myanswers, this.Rivalanswers, "負け");// 勝敗を更新
      matchMapper.updateById(match);
    }

    ArrayList<MatchInfo> matchInfo = matchInfoMapper.selectByMatchId(matchid);

    model.addAttribute("matchInfo", matchInfo);
    model.addAttribute("myanswer", playeranswer);
    model.addAttribute("loginid", loginUser_id);
    model.addAttribute("battleid", battleid);
    model.addAttribute("rivalanswer", rivalanswer);
    model.addAttribute("goalflag", goakflag);
    model.addAttribute("Formatcheak", Formatcheak);

    return "match.html";
  }
}

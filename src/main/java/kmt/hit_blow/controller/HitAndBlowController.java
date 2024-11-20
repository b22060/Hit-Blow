package kmt.hit_blow.controller;

import java.io.IOException;
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
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
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
  String Myanswers; // 自分の回答（文字列）
  String Rivalanswers;// 相手の回答（文字列）
  int battleid = 0; // 対戦相手のidを格納する
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private MatchMapper matchMapper;
  @Autowired
  private MatchInfoMapper matchInfoMapper;

  @GetMapping("step1") // テスト用
  public SseEmitter pushCount() {
    // infoレベルでログを出力する
    logger.info("pushCount");

    // finalは初期化したあとに再代入が行われない変数につける（意図しない再代入を防ぐ）
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);//
    // 引数にLongの最大値をTimeoutとして指定する

    try {
      String role = "USER";
      this.HaB.count(emitter, role);
    } catch (IOException e) {
      // 例外の名前とメッセージだけ表示する
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
      emitter.complete();
    }
    return emitter;
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
    HaB.samplechange();
    return "sample.html";
  }

  @GetMapping("/sampleSSE") // SSE通信テスト用
  public SseEmitter sampleSSE() {
    final SseEmitter sseEmitter = new SseEmitter();
    try {

      this.HaB.sample(sseEmitter);

    } catch (Exception e) {
      System.out.println("エラー発生！！");
      System.out.println(e);
    }
    return sseEmitter;
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
  public String match(@RequestParam Integer userid, ModelMap model, Principal prin) {
    String name = userMapper.selectNameByUsers(userid);// 対戦相手の名前を取得する変数
    String loginUser = prin.getName(); // ログイン名を取得
    String message = loginUser + "の秘密の数字入力を待っています。";// システムメッセージを格納する変数
    model.addAttribute("name", name);// Thymeleafで値をHTMLに渡す
    model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
    model.addAttribute("mysecret", "????");// 自分の秘密の数字は最初????のため
    model.addAttribute("rivalsecret", "????");// 相手の秘密の数字は????のため
    this.battleid = userid;// ここは一度しか経由しないから
    return "match.html";
  }

  @PostMapping("/play") // ここで対戦の処理をする
  public String play(@RequestParam Integer line1, @RequestParam Integer line2, @RequestParam Integer line3,
      @RequestParam Integer line4, ModelMap model, Principal prin) {

    int[] in = { line1, line2, line3, line4 }; // 入力を配列に格納する
    String mysecret = this.Myanswers;// 自分の？？？？と表示されている秘密の数字を格納する変数
    int myHit = 0; // 自分のHitを数える変数
    int myBlow = 0; // 自分のBlowを数える変数
    String rivalsecret = "????";// 相手の？？？？と表示されている秘密の数字を格納する変数
    int rivalHit = 0; // 相手のHitを数える変数
    int rivalBlow = 0; // 相手のBlowを数える変数
    String name = userMapper.selectNameByUsers(battleid);// 対戦相手の名前を取得する変数
    int[] Hit_Blow; // HitとBlowの値を格納する配列
    int goakflag = 0; // 正解かどうかの判定
    // int Formatcheak = 1; // 入力が正常か確認する変数

    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す

    String loginUser = prin.getName(); // ログイン名を取得
    String message = loginUser + "の数字入力を待っています。";// システムメッセージを格納する変数
    int loginUser_id = userMapper.selectIdByName(loginUser);// 自分のID取得

    if (cheak.numFormat(in) != true) { // 数値の重複があった場合
      // 重複しているため例外処理を行う。
      message = loginUser + "の数字入力を待っています。" + "エラー：数値を重複させないでください。";
      model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
      if (this.flag == 0) {// 初回の際に重複した場合、Myanswerがnullのため
        mysecret = "????";
      }
      model.addAttribute("mysecret", mysecret);
      model.addAttribute("rivalsecret", rivalsecret);
      model.addAttribute("name", name);
      return "match.html";
    }

    if (this.flag == 0) { // 初回はここに入る

      this.Myanswers = cheak.translateString(in);// ここで自分の答えを4桁の文字列にする
      mysecret = this.Myanswers;// 自分の秘密の数字が確定したため更新

      // ここからは相手の答えを生成する
      this.rivalanswer = cheak.generateNumber();// 相手の答えを生成する
      this.Rivalanswers = cheak.translateString(this.rivalanswer); // ここで相手の答えを4桁の文字列にする

      Match match = new Match(loginUser_id, battleid, this.Myanswers, this.Rivalanswers, "");// 自分のid,相手のid,自分の答え,相手の答えを格納
      matchMapper.insertMatch(match);// 1試合追加(勝敗は不明)
      this.flag = 1; // 生成は1回のみだから

      model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
      model.addAttribute("mysecret", mysecret);
      model.addAttribute("rivalsecret", rivalsecret);
      model.addAttribute("name", name);
      return "match.html";
    }
    // 以降はflag=1。つまり、秘密の数字決定後の処理を行う

    int matchid = matchMapper.selectMatchIdByuserId(loginUser_id, battleid);

    Hit_Blow = cheak.chackHit_Blow(in, this.rivalanswer);// HitとBlowを確認する
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
      message = loginUser + "の勝利です。";

      rivalsecret = this.Rivalanswers;// 相手の？？？？を開示
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

      Hit_Blow = cheak.chackHit_Blow(rivalguess, this.playeranswer);// HitとBlowの数を計算
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
      message = name + "の勝利です。";
      Match match = new Match(matchid, loginUser_id, battleid, this.Myanswers, this.Rivalanswers, "負け");// 勝敗を更新
      matchMapper.updateById(match);
    }

    ArrayList<MatchInfo> matchInfo = matchInfoMapper.selectByMatchId(matchid);

    model.addAttribute("name", name);// Thymeleafで値をHTMLに渡す
    model.addAttribute("matchInfo", matchInfo);
    model.addAttribute("debuganswer", this.rivalanswer);// デバッグ用製品版で削除すること。
    model.addAttribute("loginid", loginUser_id);
    model.addAttribute("battleid", battleid);
    model.addAttribute("rivalsecret", rivalsecret);
    model.addAttribute("goalflag", goakflag);
    model.addAttribute("message", message);
    model.addAttribute("mysecret", mysecret);
    return "match.html";
  }
}

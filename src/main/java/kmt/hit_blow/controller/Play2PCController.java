package kmt.hit_blow.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kmt.hit_blow.model.GameData;
import kmt.hit_blow.model.HitAndBlow;
import kmt.hit_blow.model.Match;
import kmt.hit_blow.service.AsyncHitAndBlow;

@Controller
public class Play2PCController {
  @Autowired
  private AsyncHitAndBlow HitAndBlow;

  private GameData user1;// user1の情報を格納する
  private GameData user2;// user2の情報を格納する
  private int turn = 1;// 奇数であればuser1の番、偶数であればuser2の番である。

  @GetMapping("/waitSSE")
  public SseEmitter waitSSE() {
    // SSE通信の実装
    final SseEmitter SseEmitter = new SseEmitter();
    try {
      this.HitAndBlow.asyncHitAndBlowWait(SseEmitter);
    } catch (Exception e) {
      System.out.println("エラー発生！！");
      System.out.println(e);
    }
    return SseEmitter;
  }

  @PostMapping("/wait")
  public String wait(@RequestParam Integer line1, @RequestParam Integer line2, @RequestParam Integer line3,
      @RequestParam Integer line4, @RequestParam Integer myid, @RequestParam Integer rivalid, ModelMap model,
      Principal prin) {
    int[] in = { line1, line2, line3, line4 };
    HitAndBlow check = new HitAndBlow();

    if (check.numFormat(in) != true) { // 数値の重複があった場合
      String rivalname = this.HitAndBlow.asyncSelectNameByUsers(rivalid);
      int formboolean = 1;
      model.addAttribute("rivalname", rivalname);// Thymeleafで値をHTMLに渡す
      model.addAttribute("myid", myid);// 自分のid
      model.addAttribute("rivalid", rivalid);// 相手のid
      model.addAttribute("formboolean", formboolean);// フォーム表示用のboolean
      return "wait.html";
    }

    if (HitAndBlow.asyncSelectIsActiveById(rivalid, myid) == "TRUE") {// 自分と相手のidでactiveの試合があるか？
      int matchid = HitAndBlow.asyncSelectMatchIdByuserId(rivalid, myid); // 相手の情報があるレコードを取り出す

      String mysecret = check.translateString(in);// int ⇒Stringへ
      this.HitAndBlow.asyncUpdateUsernum2ByMatchId(matchid, mysecret);// matchidに対してUpdate処理
      String rivalname = this.HitAndBlow.asyncSelectNameByUsers(rivalid);
      model.addAttribute("rivalname", rivalname);// 相手のid
      model.addAttribute("myid", myid);// 自分のid
      model.addAttribute("rivalid", rivalid);// 相手のid
    } else {// 自分と相手のidでactiveの試合がない場合
      String rivalname = this.HitAndBlow.asyncSelectNameByUsers(rivalid);
      String Myanswers = check.translateString(in);
      Match match = new Match(myid, rivalid, Myanswers, "", "", true);
      this.HitAndBlow.asyncInsertMatch(match);
      // final SseEmitter SseEmitter = new SseEmitter();
      // this.HitAndBlow.asyncHitAndBlow(SseEmitter);
      model.addAttribute("rivalname", rivalname);// 相手のid
      model.addAttribute("myid", myid);// 自分のid
      model.addAttribute("rivalid", rivalid);// 相手のid
    }
    return "wait.html";
  }

  @GetMapping("/match2pc") // ここで対戦の処理をする
  public String match2pc(@RequestParam Integer matchid, ModelMap model, Principal prin) {
    Match match = HitAndBlow.asyncSelectMatchById(matchid);// match情報を格納
    this.user1 = new GameData(matchid, match.getUserid1(), HitAndBlow.asyncSelectNameByUsers(match.getUserid1()),
        match.getUsernum1());
    this.user2 = new GameData(matchid, match.getUserid2(), HitAndBlow.asyncSelectNameByUsers(match.getUserid2()),
        match.getUsernum2());
    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す

    String message = cheak.generateWaitMessage(user1, user2, this.turn);// 数字入力を待っていますメッセージを生成する
    String myname = prin.getName();
    String mysecret = "";
    String rivalname = "";
    String rivalsecret = "????";
    if (myname.equals(this.user1.getName())) {
      mysecret = this.user1.getSecret();
      rivalname = this.user2.getName();
    } else {
      mysecret = this.user2.getSecret();
      rivalname = this.user1.getName();
    }

    model.addAttribute("name", myname);// 自分の名前
    model.addAttribute("rivalname", rivalname);// 相手の名前
    model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
    model.addAttribute("mysecret", mysecret);// 自分の秘密の数字（表示用）
    model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字(表示用)
    return "match2PC.html";
  }

  // @PostMapping("/play2pc") // ここで対戦の処理をする
  // public String play2pc(@RequestParam Integer line1, @RequestParam Integer
  // line2, @RequestParam Integer line3,
  // @RequestParam Integer line4, ModelMap model, Principal prin) {// CPU戦時

  // int[] in = { line1, line2, line3, line4 }; // 入力を配列に格納する

  // HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す

  // String message = loginUser + "の数字入力を待っています。";// システムメッセージを格納する変数
  // int loginUser_id = this.HitAndBlow.asyncSelectIdByName(loginUser);// 自分のID取得

  // if (cheak.numFormat(in) != true) { // 数値の重複があった場合
  // // 重複しているため例外処理を行う。
  // message = loginUser + "の数字入力を待っています。" + "エラー：数値を重複させないでください。";
  // model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
  // if (this.flag == 0) {// 初回の際に重複した場合、Myanswerがnullのため
  // mysecret = "????";
  // }
  // model.addAttribute("mysecret", mysecret);
  // model.addAttribute("rivalsecret", rivalsecret);
  // model.addAttribute("rivalname", rivalname);
  // model.addAttribute("name", loginUser);
  // return "match.html";
  // }

  // if (this.flag == 0) { // 初回はここに入る

  // this.Myanswers = cheak.translateString(in);// ここで自分の答えを4桁の文字列にする
  // this.playeranswer = in;// 自分の答えを格納する
  // mysecret = this.Myanswers;// 自分の秘密の数字が確定したため更新

  // // ここからは相手の答えを生成する
  // this.rivalanswer = cheak.generateNumber();// 相手の答えを生成する
  // this.Rivalanswers = cheak.translateString(this.rivalanswer); //
  // ここで相手の答えを4桁の文字列にする

  // Match match = new Match(loginUser_id, battleid, this.Myanswers,
  // this.Rivalanswers, "", true);// 自分のid,相手のid,自分の答え,相手の答えを格納
  // this.HitAndBlow.asyncInsertMatch(match);// 1試合追加(勝敗は不明)
  // this.flag = 1; // 生成は1回のみだから

  // model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
  // model.addAttribute("mysecret", mysecret);
  // model.addAttribute("rivalsecret", rivalsecret);
  // model.addAttribute("rivalname", rivalname);
  // model.addAttribute("name", loginUser);
  // return "match.html";
  // }
  // // 以降はflag=1。つまり、秘密の数字決定後の処理を行う

  // int matchid = this.HitAndBlow.asyncSelectMatchIdByuserId(loginUser_id,
  // battleid);

  // Hit_Blow = cheak.chackHit_Blow(in, this.rivalanswer);// HitとBlowを確認する
  // myHit = Hit_Blow[0];
  // myBlow = Hit_Blow[1];

  // String myguess = cheak.translateString(in); // ここで自分の予想を4桁の文字列にする
  // MatchInfo mymatchInfo = new MatchInfo(matchid, loginUser_id, myguess, myHit,
  // myBlow, true); // 情報を格納する
  // this.HitAndBlow.asyncInsertMatchInfo(mymatchInfo);

  // if (myHit == 4) { // Hitが4だと正解にする
  // goakflag = 1;
  // this.flag = 0;
  // message = loginUser + "の勝利です。";

  // rivalsecret = this.Rivalanswers;// 相手の？？？？を開示
  // Match match = new Match(matchid, loginUser_id, battleid, this.Myanswers,
  // this.Rivalanswers, loginUser + "の勝利!",
  // false);// 勝敗を更新
  // this.HitAndBlow.asyncUpdateById(match);
  // this.HitAndBlow.asyncUpdateActive(match);
  // this.HitAndBlow.asyncUpdateActive(mymatchInfo);
  // } else if (battleid == 3) {// battleid =3はCPUである。CPU戦の場合の処理をelse ifで記述している
  // // プレイヤーが勝利していないためcpuの手を決める

  // int[] rivalguess = new int[4];// ここでcpuの推測を格納する

  // rivalguess = cheak.generateNumber();//
  // cpuの推測（現状は完全ランダムに生成するためgenerateNumberを用いている）

  // Hit_Blow = cheak.chackHit_Blow(rivalguess, this.playeranswer);//
  // HitとBlowの数を計算
  // rivalHit = Hit_Blow[0];
  // rivalBlow = Hit_Blow[1];

  // String rivalguesshand = cheak.translateString(rivalguess);//
  // ここで相手の予想を4桁の文字列にする
  // MatchInfo rivalmatchInfo = new MatchInfo(matchid, battleid, rivalguesshand,
  // rivalHit, rivalBlow, true); // 情報を格納する
  // this.HitAndBlow.asyncInsertMatchInfo(rivalmatchInfo);
  // }

  // if (rivalHit == 4) {// Hitが4だと正解にする
  // goakflag = 1;
  // this.flag = 0;
  // message = rivalname + "の勝利です。";
  // Match match = new Match(matchid, loginUser_id, battleid, this.Myanswers,
  // this.Rivalanswers, rivalname + "の勝利!",
  // false);// 勝敗を更新
  // this.HitAndBlow.asyncUpdateById(match);
  // this.HitAndBlow.asyncUpdateActive(match);
  // this.HitAndBlow.asyncUpdateActive(mymatchInfo);
  // }

  // ArrayList<MatchInfo> matchInfo =
  // this.HitAndBlow.asyncSelectByMatchId(matchid);

  // model.addAttribute("matchInfo", matchInfo);// Thymeleafで試合情報をHTMLに渡す
  // model.addAttribute("message", message);// システムメッセージを表示するために用いる
  // model.addAttribute("goalflag", goakflag);// ゲーム終了時用のフラグ
  // model.addAttribute("debuganswer", this.rivalanswer);// デバッグ用製品版で削除すること。

  // model.addAttribute("name", loginUser);// 自身の名前を表示するために用いる
  // model.addAttribute("mysecret", mysecret);// 自身の秘密の数字部分を表示する
  // model.addAttribute("loginid", loginUser_id);// matchInfoで自身の試合情報を表示するために用いる

  // model.addAttribute("rivalname", rivalname);// 相手の名前を表示するために用いる
  // model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字部分を表示する
  // model.addAttribute("battleid", battleid);// matchInfoで相手の試合情報を表示するために用いる

  // return "match.html";
  // }

}

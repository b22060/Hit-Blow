package kmt.hit_blow.controller;

import java.security.Principal;
import java.util.ArrayList;

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
import kmt.hit_blow.model.MatchInfo;
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

  @GetMapping("/match2pc") // ここで対戦の処理初回を行う。
  public String match2pc(@RequestParam Integer matchid, ModelMap model, Principal prin) {
    Match match = HitAndBlow.asyncSelectMatchById(matchid);// match情報を格納
    this.user1 = new GameData(matchid, match.getUserid1(), HitAndBlow.asyncSelectNameByUsers(match.getUserid1()),
        match.getUsernum1());
    this.user2 = new GameData(matchid, match.getUserid2(), HitAndBlow.asyncSelectNameByUsers(match.getUserid2()),
        match.getUsernum2());
    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す

    String message = cheak.generateWaitMessage(user1, user2, this.turn);// 数字入力を待っていますメッセージを生成する
    String myname = prin.getName();
    String mysecret = cheak.getMySecret(user1, user2, myname);
    String rivalname = cheak.getRivalName(user1, user2, myname);
    String rivalsecret = "????";

    model.addAttribute("name", myname);// 自分の名前
    model.addAttribute("rivalname", rivalname);// 相手の名前
    model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
    model.addAttribute("mysecret", mysecret);// 自分の秘密の数字（表示用）
    model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字(表示用)
    return "match2PC.html";
  }

  @GetMapping("/playSSE")
  public SseEmitter playSSE() {
    // SSE通信の実装
    final SseEmitter SseEmitter = new SseEmitter();
    try {
      this.HitAndBlow.asyncHitAndBlowPlay(SseEmitter);
    } catch (Exception e) {
      System.out.println("エラー発生！！");
      System.out.println(e);
    }
    return SseEmitter;
  }

  @PostMapping("/play2pc") // ここで対戦の処理をする
  public String play2pc(@RequestParam Integer line1, @RequestParam Integer line2,
      @RequestParam Integer line3,
      @RequestParam Integer line4, ModelMap model, Principal prin) {// CPU戦時

    int[] in = { line1, line2, line3, line4 }; // 入力を配列に格納する

    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す
    int matchid = this.user1.getMatchid();
    String message = cheak.generateWaitMessage(user1, user2, this.turn);// 数字入力を待っていますメッセージを生成する
    String myname = prin.getName();
    int myid = HitAndBlow.asyncSelectIdByName(myname);
    String mysecret = cheak.getMySecret(user1, user2, myname);
    int rivalid = cheak.getRivalId(user1, user2, myname);
    String rivalname = cheak.getRivalName(user1, user2, myname);
    String rivalsecret = "????";
    int[] Hit_Blow; // HitとBlowの値を格納する配列
    int hit; // Hit数を格納
    int blow; // Blow数を格納
    int goakflag = 0; // 正解かどうかの判定

    if (cheak.numFormat(in) != true) { // 数値の重複があった場合
      // 重複しているため例外処理を行う。
      message = message + "エラー：数値を重複させないでください。";
      ArrayList<MatchInfo> matchInfo = this.HitAndBlow.asyncSelectByMatchId(matchid);

      model.addAttribute("matchInfo", matchInfo);// Thymeleafで試合情報をHTMLに渡す
      model.addAttribute("message", message);// システムメッセージを表示するために用いる
      model.addAttribute("debuganswer", cheak.translateInt(cheak.getRivalSecret(user1, user2, myname)));// デバッグ用製品版で削除すること。

      model.addAttribute("name", myname);// 自身の名前を表示するために用いる
      model.addAttribute("mysecret", mysecret);// 自身の秘密の数字部分を表示する
      model.addAttribute("loginid", myid);// matchInfoで自身の試合情報を表示するために用いる

      model.addAttribute("rivalname", rivalname);// 相手の名前を表示するために用いる
      model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字部分を表示する
      model.addAttribute("battleid", rivalid);// matchInfoで相手の試合情報を表示するために用いる
      return "match2PC.html";
    }
    if (cheak.checkTypist(user1, user2, myname, turn) != true) {// 想定外のプレイヤーから入力があった場合
      message = message + "エラー：他のプレイヤーの推理を待ってください。";
      ArrayList<MatchInfo> matchInfo = this.HitAndBlow.asyncSelectByMatchId(matchid);

      model.addAttribute("matchInfo", matchInfo);// Thymeleafで試合情報をHTMLに渡す
      model.addAttribute("message", message);// システムメッセージを表示するために用いる
      model.addAttribute("debuganswer", cheak.translateInt(cheak.getRivalSecret(user1, user2, myname)));// デバッグ用製品版で削除すること。

      model.addAttribute("name", myname);// 自身の名前を表示するために用いる
      model.addAttribute("mysecret", mysecret);// 自身の秘密の数字部分を表示する
      model.addAttribute("loginid", myid);// matchInfoで自身の試合情報を表示するために用いる

      model.addAttribute("rivalname", rivalname);// 相手の名前を表示するために用いる
      model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字部分を表示する
      model.addAttribute("battleid", rivalid);// matchInfoで相手の試合情報を表示するために用いる
      return "match2PC.html";
    }

    // 以降から数字判定処理を行う

    Hit_Blow = cheak.chackHit_Blow(in, cheak.translateInt(cheak.getRivalSecret(user1, user2, myname)));
    hit = Hit_Blow[0];
    blow = Hit_Blow[1];
    String myguess = cheak.translateString(in); // ここで自分の予想を4桁の文字列にする
    MatchInfo mymatchInfo = new MatchInfo(user1.getMatchid(), myid, myguess, hit, blow, true); // 情報を格納する

    if (hit == 4) { // Hitが4だと正解にする
      goakflag = 1;
      this.turn = 1;// 初期のターンに戻す
      message = myname + "の勝利です。"; // 後で直すToDO
      this.HitAndBlow.asyncInsertMatchInfoFor2pc(mymatchInfo, myid, message, goakflag); // MatchInfoの格納及びゲーム内変数を引数で渡す

      rivalsecret = cheak.getRivalSecret(user1, user2, myname);// 相手の？？？？を開示
      Match match = new Match(user1.getMatchid(), myid, rivalid, mysecret, rivalsecret,
          myname + "の勝利!",
          false);// 勝敗を更新
      this.HitAndBlow.asyncUpdateById(match);
      this.HitAndBlow.asyncUpdateActive(match);
      this.HitAndBlow.asyncUpdateActive(mymatchInfo);

      ArrayList<MatchInfo> matchInfo = this.HitAndBlow.asyncSelectByMatchId(matchid);

      model.addAttribute("matchInfo", matchInfo);// Thymeleafで試合情報をHTMLに渡す
      model.addAttribute("message", message);// システムメッセージを表示するために用いる
      model.addAttribute("goalflag", goakflag);// ゲーム終了時用のフラグ
      model.addAttribute("debuganswer", cheak.translateInt(cheak.getRivalSecret(user1, user2, myname)));// デバッグ用製品版で削除すること。

      model.addAttribute("name", myname);// 自身の名前を表示するために用いる
      model.addAttribute("mysecret", mysecret);// 自身の秘密の数字部分を表示する
      model.addAttribute("loginid", myid);// matchInfoで自身の試合情報を表示するために用いる

      model.addAttribute("rivalname", rivalname);// 相手の名前を表示するために用いる
      model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字部分を表示する
      model.addAttribute("battleid", rivalid);// matchInfoで相手の試合情報を表示するために用いる

      return "match2PC.html";

    }

    this.turn++;// 相手の数字入力にする必要があるためインクリメントする
    message = cheak.generateWaitMessage(user1, user2, goakflag);// メッセージ文を更新

    this.HitAndBlow.asyncInsertMatchInfoFor2pc(mymatchInfo, myid, message, goakflag); // MatchInfoの格納及びゲーム内変数を引数で渡す

    ArrayList<MatchInfo> matchInfo = this.HitAndBlow.asyncSelectByMatchId(matchid);

    model.addAttribute("matchInfo", matchInfo);// Thymeleafで試合情報をHTMLに渡す
    model.addAttribute("message", message);// システムメッセージを表示するために用いる
    model.addAttribute("goalflag", goakflag);// ゲーム終了時用のフラグ
    model.addAttribute("debuganswer", cheak.translateInt(cheak.getRivalSecret(user1, user2, myname)));// デバッグ用製品版で削除すること。

    model.addAttribute("name", myname);// 自身の名前を表示するために用いる
    model.addAttribute("mysecret", mysecret);// 自身の秘密の数字部分を表示する
    model.addAttribute("loginid", myid);// matchInfoで自身の試合情報を表示するために用いる

    model.addAttribute("rivalname", rivalname);// 相手の名前を表示するために用いる
    model.addAttribute("rivalsecret", rivalsecret);// 相手の秘密の数字部分を表示する
    model.addAttribute("battleid", rivalid);// matchInfoで相手の試合情報を表示するために用いる

    return "match2PC.html";
  }

}

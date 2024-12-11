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
  public String play2pc(@RequestParam Integer line1, @RequestParam Integer line2, @RequestParam Integer line3,
      @RequestParam Integer line4, ModelMap model, Principal prin) {// CPU戦時

    int[] in = { line1, line2, line3, line4 }; // 入力を配列に格納する

    HitAndBlow cheak = new HitAndBlow(); // Hit_Blowクラスのメソッドを呼び出す
    String message = cheak.generateWaitMessage(user1, user2, this.turn);// 数字入力を待っていますメッセージを生成する
    String myname = prin.getName();
    String mysecret = cheak.getMySecret(user1, user2, myname);
    String rivalname = cheak.getRivalName(user1, user2, myname);
    String rivalsecret = "????";

    if (cheak.numFormat(in) != true) { // 数値の重複があった場合
      // 重複しているため例外処理を行う。
      message = message + "エラー：数値を重複させないでください。";
      model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す

      model.addAttribute("mysecret", mysecret);
      model.addAttribute("rivalsecret", rivalsecret);
      model.addAttribute("rivalname", rivalname);
      model.addAttribute("name", myname);
      return "match2PC.html";
    }
    if (cheak.checkTypist(user1, user2, myname, turn) != true) {// 想定外のプレイヤーから入力があった場合
      message = message + "エラー：他のプレイヤーの推理を待ってください。";
      model.addAttribute("message", message);// Thymeleafで値をHTMLに渡す
      model.addAttribute("mysecret", mysecret);
      model.addAttribute("rivalsecret", rivalsecret);
      model.addAttribute("rivalname", rivalname);
      model.addAttribute("name", myname);
      return "match2PC.html";
    }
    return "index.html";
  }

}

package kmt.hit_blow.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kmt.hit_blow.model.HitAndBlow;
import kmt.hit_blow.model.Match;
import kmt.hit_blow.service.AsyncHitAndBlow;

@Controller
public class Play2PCController {
  @Autowired
  private AsyncHitAndBlow HitAndBlow;

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

  @GetMapping("/play2pc") // ここで対戦の処理をする
  public String play2pc(@RequestParam Integer matchid) {
    return "match.html";
  }

}

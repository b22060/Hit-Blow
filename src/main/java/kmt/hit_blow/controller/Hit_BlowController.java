package kmt.hit_blow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.ui.Model;

@Controller
public class Hit_BlowController {

  @GetMapping("/hit-blow")
  public String sample31() {
    return "hit-blow.html";
  }

}

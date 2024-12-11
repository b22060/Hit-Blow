package kmt.hit_blow.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kmt.hit_blow.model.MatchInfoMapper;
import kmt.hit_blow.model.MatchInfo;
import kmt.hit_blow.model.MatchMapper;
import kmt.hit_blow.model.SSEMatch;
import kmt.hit_blow.model.Match;
import kmt.hit_blow.model.UserMapper;
import kmt.hit_blow.model.User;

@Service
public class AsyncHitAndBlow {

  private int customerCount = 1;// customerロール用カウンター
  private int sellerCount = 1;// sellerロール用カウンター
  private int hogehoge = 0;// ０→1→0と遷移する サンプル用

  private final Logger logger = LoggerFactory.getLogger(AsyncHitAndBlow.class);

  private int matchid;
  private boolean updateflag = false;

  private int insertedid;
  private String message;
  private int goalflag;

  @Autowired
  private UserMapper userMapper;
  @Autowired
  private MatchMapper matchMapper;
  @Autowired
  private MatchInfoMapper matchInfoMapper;

  /* UserMapperを使った処理一覧 */
  public ArrayList<User> asyncSelectAllByUsers() {// userのすべて
    return this.userMapper.selectAllByUsers();
  }

  public String asyncSelectNameByUsers(int userid) {// userIdでnameを取得
    return this.userMapper.selectNameByUsers(userid);
  }

  public int asyncSelectIdByName(String userName) {// usernameでuserIdを取得
    return this.userMapper.selectIdByName(userName);
  }

  /* MatchMapperを使った処理一覧 */
  public ArrayList<Match> asyncSelectAllNotActiveByMatches() {// isActiveがfalseの試合
    return this.matchMapper.selectAllNotActiveByMatches();
  }

  public ArrayList<Match> asyncSelectAllActiveByMatches() {// isActiveがtrueの試合
    return this.matchMapper.selectAllActiveByMatches();
  }

  public Match asyncSelectMatchById(int matchid) {// isActiveがfalseの試合
    return this.matchMapper.selectMatchById(matchid);
  }

  public int asyncSelectUserId1ByMatchId(int matchid) {// isActiveがfalseの試合
    return this.matchMapper.selectUserId1ByMatchId(matchid);
  }

  public int asyncSelectUserId2ByMatchId(int matchid) {// isActiveがfalseの試合
    return this.matchMapper.selectUserId2ByMatchId(matchid);
  }

  public int asyncSelectMatchIdByuserId(int userid1, int userid2) {// isActiveがfalseの試合
    return this.matchMapper.selectMatchIdByuserId(userid1, userid2);
  }

  public String asyncSelectIsActiveById(int userid1, int userid2) {// isActiveがfalseの試合
    return this.matchMapper.selectIsActiveByuserId(userid1, userid2);
  }

  public boolean asyncUpdateUsernum2ByMatchId(int matchid, String usernum2) {// user2の秘密の数字をUpdateする
    // ここで非同期処理のグローバル変数が更新される。
    this.updateflag = true;
    this.matchid = matchid;
    return this.matchMapper.updateUsernum2ByMatchId(matchid, usernum2);
  }

  public void asyncInsertMatch(Match match) {// isActiveがfalseの試合
    this.matchMapper.insertMatch(match);
  }

  public void asyncUpdateById(Match match) {// isActiveがfalseの試合
    this.matchMapper.updateById(match);
  }

  public boolean asyncUpdateActive(Match match) {// isActiveがfalseの試合
    return this.matchMapper.updateActive(match);
  }

  public ArrayList<Integer> asyncSelectMatchIdByIsActive(int user2id) {// isActiveがfalseの試合
    return this.matchMapper.selectMatchIdByIsActive(user2id);
  }

  /* MatchInfoMapperを使った処理一覧 */

  public ArrayList<MatchInfo> asyncSelectByMatchId(int matchid) {// isActiveがfalseの試合
    return this.matchInfoMapper.selectByMatchId(matchid);
  }

  public void asyncInsertMatchInfo(MatchInfo matchinfo) {// 新たなmatchinfo行を挿入する
    this.matchInfoMapper.insertMatchInfo(matchinfo);
  }

  public boolean asyncUpdateActive(MatchInfo matchinfo) {// isActiveがfalseの試合
    return this.matchInfoMapper.updateActive(matchinfo);
  }

  public void asyncInsertMatchInfoFor2pc(MatchInfo matchinfo, int insertedid, String message, int goalflag) {// 新たなmatchinfo行を挿入する
    this.updateflag = true;// 2PC戦のための更新
    this.insertedid = insertedid;// 挿入したuseridを格納する
    this.message = message;
    this.goalflag = goalflag;
    this.asyncInsertMatchInfo(matchinfo);
  }

  @Async
  public void asyncHitAndBlowWait(SseEmitter emitter) {// Wait.htmlにおけるSSE通信部分
    logger.info("wait.htmlの処理開始");
    try {
      while (true) {

        if (this.updateflag == false) {// 変化なし
          TimeUnit.MILLISECONDS.sleep(50);
          continue;
        }
        // updateflag がtrueのとき以下の処理が実行
        TimeUnit.MILLISECONDS.sleep(50);

        Match match = this.asyncSelectMatchById(this.matchid);

        emitter.send(match);
        logger.info("成功！！");
        TimeUnit.MILLISECONDS.sleep(5);
        updateflag = false;

        TimeUnit.MILLISECONDS.sleep(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("エラー：" + e);
    } finally {
      emitter.complete();
    }
    System.out.println("asyncHitAndBlow complete");
  }

  @Async
  public void asyncHitAndBlowPlay(SseEmitter emitter) {// Wait.htmlにおけるSSE通信部分
    logger.info("match2pc.htmlの処理開始");
    try {
      while (true) {

        if (this.updateflag == false) {// 変化なし
          TimeUnit.MILLISECONDS.sleep(50);
          continue;
        }
        // updateflag がtrueのとき以下の処理が実行
        TimeUnit.MILLISECONDS.sleep(50);

        ArrayList<MatchInfo> matchInfo = this.asyncSelectByMatchId(matchid);
        SSEMatch info = new SSEMatch(matchInfo, this.insertedid, this.message, this.goalflag);

        emitter.send(info);
        logger.info("成功！！");
        TimeUnit.MILLISECONDS.sleep(5);
        updateflag = false;

        TimeUnit.MILLISECONDS.sleep(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("エラー：" + e);
    } finally {
      emitter.complete();
    }
    System.out.println("asyncHitAndBlow complete");
  }

  @Async
  public void count(SseEmitter emitter, String role) throws IOException {
    logger.info("AsyncCount58.count");
    try {
      while (true) {
        int counter = 0;
        // CUSTOMERとSELLERでカウンタを分ける
        // この2つ以外のロール場合は常にcounter=0
        if (role.equals("USER")) {
          counter = customerCount;
          customerCount++;
        } else if (role.equals("USER")) {
          counter = sellerCount;
          sellerCount++;
        }
        // ロールごとのカウンタとロール名を送る
        emitter.send(SseEmitter.event()
            .data(counter)
            .id(role));
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void samplechange() {
    if (hogehoge == 0) {
      hogehoge = 1;
    } else {
      hogehoge = 0;
    }
  }

  @Async
  public void sample(SseEmitter emitter) {

    logger.info("AsyncCount58.count");
    try {
      while (true) {
        if (hogehoge == 1) {
          TimeUnit.MILLISECONDS.sleep(50);
          continue;
        }
        TimeUnit.MILLISECONDS.sleep(50);
        if (hogehoge == 0) {

          emitter.send("SSEを通信開始");
          System.out.println("確認用！！！！！");
          TimeUnit.MILLISECONDS.sleep(5);
          this.samplechange();
        }

        TimeUnit.SECONDS.sleep(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

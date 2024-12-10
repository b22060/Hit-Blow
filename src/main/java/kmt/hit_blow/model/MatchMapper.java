package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {
  @Select("SELECT * from matches where isActive=FALSE")
  ArrayList<Match> selectAllNotActiveByMatches();

  @Select("SELECT * from matches where isActive=TRUE")
  ArrayList<Match> selectAllActiveByMatches();

  @Select("SELECT * FROM matches WHERE matchid = #{matchid}")
  Match selectMatchById(int matchid);

  @Select("SELECT userid1 FROM matches WHERE matchid = #{matchid}")
  int selectUserId1ByMatchId(int matchid);

  @Select("SELECT userid2 FROM matches WHERE matchid = #{matchid}")
  int selectUserId2ByMatchId(int matchid);

  // @Select("SELECT userid2 FROM matches WHERE userid1 = #{userid1} and matchid =
  // #{matchid}")
  // int selectopponentsIdById(int userid1, int matchid);// 対戦相手のidを取得

  @Select("SELECT matchid FROM matches WHERE userid1 = #{userid1} and userid2 = #{userid2} ORDER BY matchid DESC LIMIT 1")
  // useridからmatchidを入手
  int selectMatchIdByuserId(int userid1, int userid2);

  @Select("SELECT isActive FROM matches WHERE userid1 = #{userid1} and userid2 = #{userid2} and isActive = TRUE")
  // useridからmatchidを入手
  String selectIsActiveByuserId(int userid1, int userid2);

  @Select("SELECT user1Hand FROM matches WHERE matchid = #{matchid}")
  // useridからmatchidを入手
  String selectUser1HandByMatchId(int matchid);

  @Insert("INSERT INTO matches (userid1,userid2,usernum1,usernum2,judge,isActive) VALUES (#{userid1},#{userid2},#{usernum1},#{usernum2},#{judge},#{isActive});")
  void insertMatch(Match match);// optionsは消したおそらくmatchidとかにしたら行けるはず

  @Update("UPDATE matches SET judge=#{judge} WHERE matchid = #{matchid}")
  void updateById(Match match);

  @Update("UPDATE matches SET isActive = FALSE where isActive = TRUE and matchid=#{matchid}") // FALSEにする
  boolean updateActive(Match match);

  @Select("SELECT userid1 FROM matches WHERE userid2 =#{userid2} and usernum2 =''and isActive=TRUE")
  ArrayList<Integer> selectMatchIdByIsActive(int user2id);
}

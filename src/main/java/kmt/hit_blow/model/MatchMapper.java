package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {
  @Select("SELECT * from matches")
  ArrayList<Match> selectAllBymatches();

  @Select("SELECT * FROM matches WHERE matchid = #{matchid}")
  Match selectMatchById(int matchid);

  @Select("SELECT userid2 FROM matches WHERE userid1 = #{userid1} and matchid = #{matchid}")
  int selectopponentsIdById(int userid1, int matchid);// 対戦相手のidを取得

  @Select("SELECT matchid FROM matches WHERE userid1 = #{userid1} and userid2 = #{userid2} ORDER BY matchid DESC LIMIT 1")
  // useridからmatchidを入手
  int selectMatchIdByuserId(int userid1, int userid2);

  @Insert("INSERT INTO matches (userid1,userid2,usernum1,usernum2,judge) VALUES (#{userid1},#{userid2},#{usernum1},#{usernum2},#{judge});")
  void insertMatch(Match match);// optionsは消したおそらくmatchidとかにしたら行けるはず

  @Update("UPDATE matches SET judge=#{judge} WHERE matchid = #{matchid}")
  void updateById(Match match);
}

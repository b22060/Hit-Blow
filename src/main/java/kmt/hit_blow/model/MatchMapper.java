package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {
  @Select("SELECT * from matches")
  ArrayList<Match> selectAllBymatches();

  @Select("SELECT * FROM matches WHERE matchid = #{matchid}")
  Match selectMatchById(int matchid);

  @Select("SELECT matchid FROM matches WHERE userid1 = #{userid1} and userid2 = #{userid2} ORDER BY matchid DESC LIMIT 1")
  //useridからmatchidを入手
  int selectMatchIdByuserId(int userid1,int userid2);

  @Insert("INSERT INTO matches (userid1,userid2,usernum1,usernum2,judge) VALUES (#{userid1},#{userid2},#{usernum1},#{usernum2},#{judge});")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertMatch(Match match);
}

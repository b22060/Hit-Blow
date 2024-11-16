package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchInfoMapper {
  @Select("SELECT * FROM matchinfo WHERE matchid = #{matchid}")
  ArrayList<MatchInfo> selectByMatchId(int matchid);

  @Insert("INSERT INTO MatchInfo (matchid,userid,guess,hit,blow) VALUES (#{matchid},#{userid},#{guess},#{hit},#{blow});") // ここで1手ずつ格納する
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertMatchInfo(MatchInfo matchinfo);
}

package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchInfoMapper {
  @Select("SELECT * FROM matchinfo WHERE matchid = #{matchid}")
  ArrayList<MatchInfo> selectByMatchId(int matchid);
}

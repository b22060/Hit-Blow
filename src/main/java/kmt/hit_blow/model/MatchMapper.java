package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {
  @Select("SELECT * from matches")
  ArrayList<Match> selectAllBymatches();

  @Select("SELECT * FROM matches WHERE matchid = #{matchid}")
  Match selectMatchById(int matchid);
}

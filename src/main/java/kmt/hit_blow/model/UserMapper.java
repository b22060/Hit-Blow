package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("SELECT * from users")
  ArrayList<User> selectAllByUsers();

  @Select("SELECT name from users where userid =#{userid}")
  String selectNameByUsers(int userid);

}

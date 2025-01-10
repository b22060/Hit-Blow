package kmt.hit_blow.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("SELECT * from users")
  ArrayList<User> selectAllByUsers();

  @Select("SELECT * from users except SELECT * from users where name = #{name}")
  ArrayList<User> selectExceptByloginUsers(String loginUser);

  @Select("SELECT name from users where userid =#{userid}")
  String selectNameByUsers(int userid);

  @Select("SELECT userid from users where name = #{name}") // 特定の名前で特定のidを表示
  int selectIdByName(String userName);

}

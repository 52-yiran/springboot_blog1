package com.blog.mapper;

import com.blog.pojo.User;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface UserMapper extends Mapper<User> {

  @Select("SELECT DISTINCT code FROM m_permission WHERE id IN (SELECT permission_id FROM m_user_permission WHERE user_id = #{id}) ")
  public Set<String> findPermissionsByUserId(Long id);

}

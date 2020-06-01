package com.blog.service;

import com.blog.exception.ExceptionEnums;
import com.blog.exception.MyException;
import com.blog.mapper.UserMapper;
import com.blog.pojo.User;
import org.hibernate.validator.constraints.EAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

//@Transactional 不能要
// 在整合shiro的过程中 我在service 层加事物的注解会报错 The bean 'userService' could not be injected as a 'com.blog.service.UserService' because it is a JDK dynamic proxy that implements:
@Service
public class UserService {

  @Autowired
  private UserMapper userMapper;


  public User findById(Long id) {
    User user = userMapper.selectByPrimaryKey(id);
    if(user == null){
      throw new MyException(ExceptionEnums.USER_NOT_FOUND);
    }
    return user;
  }

  public User findOneUser(String username) {
    User user =new User();
    user.setUsername(username);
    user = userMapper.selectOne(user);
    if (user == null){
      //throw new MyException(ExceptionEnums.USER_NOT_FOUND);
    }
    return user;
  }

  /**
   *
   * @param id 用户id
   * @return
   */
  public Set<String> findPermissionsByUserId(Long id){
    Set<String> list= userMapper.findPermissionsByUserId(id);
    return list;
  }
}

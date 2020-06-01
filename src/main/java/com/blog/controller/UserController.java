package com.blog.controller;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.blog.exception.ExceptionEnums;
import com.blog.exception.MyException;
import com.blog.pojo.AccountProfile;
import com.blog.pojo.LoginUser;
import com.blog.pojo.User;
import com.blog.service.UserService;
import com.blog.utils.JwtUtils;
import com.blog.vo.Result;
import net.sf.saxon.ma.map.MapFunctionSet;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestMapping("/user")
@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtils jwtUtils;

  @RequiresAuthentication //方法上加认证 需要登录
  @GetMapping("/{id}")
  public Result<User> findById(@PathVariable("id") Long id){
    Subject subject = SecurityUtils.getSubject();
    PrincipalCollection principals = subject.getPrincipals();
    AccountProfile primaryPrincipal = (AccountProfile) principals.getPrimaryPrincipal();

    AccountProfile principal = (AccountProfile) subject.getPrincipal();
    System.out.println(primaryPrincipal == principal);//true

    User user =userService.findById(id);
    return new Result<>(ExceptionEnums.OK,user);

  }

  /**
   *  测试异常的情况
   * @param user
   * @return
   */
  @PostMapping("/save")
  public Result save(@Validated @RequestBody User user) {
    return new Result<>(200,"成功",user);
  }

  /**
   * 登录
   * @param loginUser
   * @param response
   * @return
   */
  @PostMapping("/login")
  public Result login(@Validated @RequestBody LoginUser loginUser, HttpServletResponse response) {

    User oneUser = userService.findOneUser(loginUser.getUsername());
    Assert.notNull(oneUser,"用户不存在");

    if(!oneUser.getPassword().equals(SecureUtil.md5(loginUser.getPassword()))){
      throw new MyException(ExceptionEnums.FAIL);// 密码错误
    }
    // 生成jwt token
    String jwtToken = jwtUtils.generateToken(oneUser.getId());
    response.setHeader("Authorization",jwtToken);
    response.setHeader("Access-control-Expose-Headers", "Authorization");
    Map<Object, Object> map = MapUtil.builder().put("id", oneUser.getId())
        .put("username", oneUser.getUsername())
        .put("avatar", oneUser.getAvatar())
        .put("email", oneUser.getEmail())
        .map();
    return new Result<>(200,"登录成功",map );
  }

  /**
   * 退出
   * @return
   */
  @RequiresAuthentication// 需要登录才能退出
  @GetMapping("/logout")
  public Result logout() {
    SecurityUtils.getSubject().logout();
    return new Result(200,"退出成功");
  }

  /**
   * 查询客户拥有的权限
   * @param id
   * @return
   */
  @GetMapping("/perms/{id}")
  public Result findPermissionsByUserId(@PathVariable("id") Long id) {
    Set<String> permissionsByUserId = userService.findPermissionsByUserId(id);
    return new Result<>(200,"查询成功",permissionsByUserId);
  }
}

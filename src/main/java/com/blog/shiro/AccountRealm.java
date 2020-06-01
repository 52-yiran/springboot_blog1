package com.blog.shiro;

import com.blog.pojo.AccountProfile;
import com.blog.pojo.User;
import com.blog.service.UserService;
import com.blog.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AccountRealm extends AuthorizingRealm {

  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  private UserService userService;

  // 是否支持JwtToken
  @Override
  public boolean supports(AuthenticationToken token) {
    return token instanceof JwtToken;
  }

  /**
   * 执行授权逻辑
   * @param principalCollection
   * @return
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    //1.获取已认证的用户数据 存的是user  就强制转化成user
    AccountProfile primaryPrincipal = (AccountProfile) principalCollection.getPrimaryPrincipal();
    //2.根据用户数据获取用户的权限信息（所有角色，所有权限)
    SimpleAuthorizationInfo info =new SimpleAuthorizationInfo();
    // 查询用户拥有的权限
    Set<String> permissionsByUserId = userService.findPermissionsByUserId(primaryPrincipal.getId());
    info.setStringPermissions(permissionsByUserId);
    //查询用户拥有的角色

    return info;
  }

  /**
   * 执行认证逻辑
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
    /*
     *subject.login(token);  由于这个方法回来到这里做认证操作
     *
     * */
    // 上面判断了原型上是不是一个类型 这里进行强转
    JwtToken jwtToken = (JwtToken) authenticationToken;
    String principal = (String) jwtToken.getPrincipal();
    Claims claim= jwtUtils.getClaimByToken(principal);// 用jwtutil工具获取Claims
    String userId = claim.getSubject();// 在生成的jwt时候就这用这种 主键id作为主题
    User user = userService.findById(Long.parseLong(userId));
    if(user == null){
      //用户名不存在
      throw new UnknownAccountException("用户名不存在");
    }
    if(user.getStatus() == -1){
      throw new LockedAccountException("账户已被锁定");
    }
    AccountProfile accountProfile = new AccountProfile();

    BeanUtils.copyProperties(user,accountProfile);
    return new SimpleAuthenticationInfo(accountProfile,jwtToken.getCredentials(),getName());// 第一个是用户的对象,第二个是密码可以随便写, 第三个是realm 的名字


  }
}

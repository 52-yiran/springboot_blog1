package com.blog.shiro;


import com.alibaba.fastjson.JSON;
import com.blog.utils.JwtUtils;
import com.blog.vo.Result;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {
  @Autowired
  private JwtUtils jwtUtils;


  @Override
  protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
    //WebUtils.toHttp(request) 需要把ServletRequest 转化成http 形式的
    HttpServletRequest httpServletRequest = WebUtils.toHttp(request);

    String jwt = httpServletRequest.getHeader("Authorization");
    if(StringUtils.isEmpty(jwt)){
      return null;
    }
    // 拿到这个jwt 后面进行校验
    return new JwtToken(jwt);
  }

  @Override         // 拒绝访问
  protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
    //WebUtils.toHttp(request) 需要把ServletRequest 转化成http 形式的
    HttpServletRequest httpServletRequest = WebUtils.toHttp(servletRequest);

    String jwt = httpServletRequest.getHeader("Authorization");
    if(StringUtils.isEmpty(jwt)){
      // 不拦截
      return true; // 拒绝访问
    }else {
      // 校验
      Claims claimByToken = jwtUtils.getClaimByToken(jwt);
      // 判断是否已过期
      if(claimByToken == null || jwtUtils.isTokenExpired(claimByToken.getExpiration())){
        throw new ExpiredCredentialsException("token已失效,请重新登录");
      }

      // 执行登录  有登录成功和失败的情况 下面需要重写登录失败
      return executeLogin(servletRequest,servletResponse);

    }
  }

  //处理登录失败的逻辑
  @Override
  protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
    HttpServletResponse httpServletResponse =(HttpServletResponse) response;
    Throwable throwable = e.getCause()==null ? e : e.getCause();

    Result objectResult = new Result(400,throwable.getMessage());

    String stringResult = JSON.toJSONString(objectResult);

    try {
      httpServletResponse.getWriter().write(stringResult);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    return false;
  }

  /**
   *  跨域处理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
    HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
    HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
    httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
    httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
    httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
    // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
    if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
      httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
      return false;
    }

    return super.preHandle(request, response);
  }
}

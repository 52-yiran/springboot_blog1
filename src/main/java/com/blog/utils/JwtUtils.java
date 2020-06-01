package com.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * jwt工具类
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "markerhub.jwt")
public class JwtUtils {

  private String secret;
  private long expire;
  private String header;

  /**
   * 生成jwt token
   */
  public String generateToken(long userId) {
    Date nowDate = new Date();
    //过期时间
    Date expireDate = new Date(nowDate.getTime() + expire * 1000);

    return Jwts.builder()
        .setHeaderParam("typ", "JWT")
        .setSubject(userId+"") // 主题
        .setIssuedAt(nowDate)  // 发表时间
        .setExpiration(expireDate) // 过期时间
        .signWith(SignatureAlgorithm.HS512, secret) // 加密方式, 秘钥
        .compact();
  }

  public Claims getClaimByToken(String token) {
    try {
      return Jwts.parser()
          .setSigningKey(secret)
          .parseClaimsJws(token)
          .getBody();
    }catch (Exception e){
      log.debug("validate is token error ", e);
      return null;
    }
  }

  /**
   * token是否过期
   * @return  true：过期
   */
  public boolean isTokenExpired(Date expiration) {
    return expiration.before(new Date());
  }
}


package com.blog.pojo;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Table(name = "m_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "昵称不能为空")
  private String username;

  private String avatar;

  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  private String password;

  private Integer status;

  private LocalDateTime created;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

}

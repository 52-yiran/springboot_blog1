package com.blog.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "m_permission_user")
public class PermissionUser {


  @Column(name = "permission_id")
  private Long permissionId;

  @Column(name = "user_id")
  private Long userId;
}

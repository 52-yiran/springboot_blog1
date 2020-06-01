package com.blog.pojo;


import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Table(name = "m_permission")
public class Permission implements Serializable {
  @Id
  private Long id;

  private String code;


}

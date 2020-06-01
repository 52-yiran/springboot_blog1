package com.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.internal.runners.statements.Fail;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ExceptionEnums {
  OK(200,"查询成功!"),
  USER_NOT_FOUND(400,"用户没有找到!"),
  CATEGORY_CODE_NOT_FOUND(404,"商品分类为找到!"),
  FAIL(400,"密码错误")
  ;
  private int code;
  private String msg;
}

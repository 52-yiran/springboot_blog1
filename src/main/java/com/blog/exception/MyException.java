package com.blog.exception;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MyException extends RuntimeException {
  private int status;
  private String message;
  private LocalDateTime timestamp;


  public MyException(ExceptionEnums exceptionEnums){
    this.status = exceptionEnums.getCode();
    this.message = exceptionEnums.getMsg();
    this.timestamp= LocalDateTime.now();
  }
}

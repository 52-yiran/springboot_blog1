package com.blog.exception;

import com.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestControllerAdvice // 默认拦截所有的controller
public class ExceptionCatch {

  @ExceptionHandler(MyException.class)
  public Result handleException(MyException my){
    String message = my.getMessage();
    int status = my.getStatus();
    LocalDateTime timestamp = my.getTimestamp();
    log.debug("运行时异常");
    return new Result(status,message,timestamp);
  }


  @ExceptionHandler(ShiroException.class)
  public Result handleException1(ShiroException e){
    log.info("shiro运行时异常",e);
    return new Result(401,e.getMessage());// token 过期了 用户名不存在,账户被锁定
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Result handleException2(MethodArgumentNotValidException e){
    log.debug("实体校验异常",e);
    List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
    Optional<ObjectError> first = allErrors.stream().findFirst();
    ObjectError objectError = first.get();
    return new Result(400,objectError.getDefaultMessage());//
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalAccessError.class)
  public Result handleException3(IllegalAccessError e){
    log.debug("断言异常",e);

    return new Result(400,e.getMessage());//
  }

}

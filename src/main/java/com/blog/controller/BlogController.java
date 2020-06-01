package com.blog.controller;

import com.blog.exception.ExceptionEnums;
import com.blog.pojo.AccountProfile;
import com.blog.pojo.Blog;
import com.blog.service.BlogService;
import com.blog.vo.PageResult;
import com.blog.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.Assert;

import java.time.LocalDateTime;

@RequestMapping("/blog")
@RestController
public class BlogController {
  @Autowired
  private BlogService blogService;


  @GetMapping("/list")
  public Result list(
      @RequestParam(value = "page",defaultValue = "1",required = false) Integer page,
      @RequestParam(value = "size",defaultValue = "5",required = false) Integer size
  ) {
    PageResult<Blog> listByPage = blogService.findListByPage(page, size);

    return new Result<>(ExceptionEnums.OK,listByPage);
  }

  /*
  * eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTkwOTcyMDc0LCJleHAiOjE1OTE1NzY4NzR9.HFyMy0hlX6k94_S7E6vjvWHADI8qNBZwbsRrIRRtvpxWrCi-fp3snY_kAthqUNpF-LthqLWDHgpIYlyYgXD2tw
  * eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTkwOTkxNjM0LCJleHAiOjE1OTE1OTY0MzR9.HmxI1ycfDR-7EbLRNUFqBim08DhbE3pB0X9REJBDoRhTDYV5kJWgg0gOExNUWpPrE-nnZ34DfdiVJmMYQoZdTQ
  * 有一个问题 这次申请了一个token 后上一个token 还可以用
  * */

  @RequiresPermissions("user:add") //根据用户查询他的权限
  @RequiresAuthentication // 需要登录验证的
  @GetMapping("/{id}")
  public Result<Blog> detail(@PathVariable(name = "id") Long id) {
    Blog blog = blogService.getById(id);
    Assert.notNull(blog, "该博客已被删除");

    return new Result<>(ExceptionEnums.OK,blog);
  }


  @RequiresAuthentication
  @PostMapping("/edit")
  public Result edit(@Validated @RequestBody Blog blog) {
    Blog temp = null;
    // 修改
    // 或者当前登录的用户对象
    AccountProfile principal = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    //下面这个获取对象 授权哪里用
    //AccountProfile primaryPrincipal = (AccountProfile) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
    //System.out.println("相等:"+(primaryPrincipal == principal));
    if(blog.getId() != null) {
      temp = blogService.getById(blog.getId());
      // 只能编辑自己的文章
      //在执行认证哪里存的对象                       AccountProfile
      Assert.isTrue(temp.getUserId().longValue() == principal.getId(), "没有权限编辑");

    } else {
      // 新增
      temp = new Blog();
      temp.setUserId(principal.getId());
      temp.setCreated(LocalDateTime.now());
      temp.setStatus(0);
    }
    BeanUtils.copyProperties(blog,temp,"id","userId","created","status");// 如果temp 中有这几个属性就不会复制过来
    blogService.save(temp);
    return new Result(ExceptionEnums.OK);
  }
}

package com.blog.service;


import com.blog.mapper.BlogMappper;
import com.blog.pojo.Blog;
import com.blog.vo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BlogService {
  @Autowired
  private BlogMappper blogMappper;

  public PageResult<Blog> findListByPage(Integer page, Integer size) {
    PageHelper.startPage(page,size);
    Example example =new Example(Blog.class);
    // 排序字段 按时间 倒序
    //Example.Criteria criteria = example.createCriteria();// 条件对象 什么等于什么 相似这些
    example.setOrderByClause("created DESC");
    // SELECT id,user_id,title,description,content,created,status FROM m_blog order by created DESC LIMIT ?
    //where name like "%x%" or letter == "x" ORDER BY id DESC;
    /*
    * example.createCriteria().orLike("name","%"+key+"%")// "name" 类中的属性名 也就是表中的属性
                             .orEqualTo("letter",key.toUpperCase()) ;
    */
    // 查询结果
    List<Blog> blogs = blogMappper.selectByExample(example);
    // 解析分页结果
    PageInfo<Blog> pageInfo =new PageInfo<>(blogs);
    return new PageResult<>(pageInfo.getTotal(),pageInfo.getPages(),blogs);

  }

  public Blog getById(Long id) {
    Blog blog = blogMappper.selectByPrimaryKey(id);
    return blog;
  }

  // 更新或者新增
  public void save(Blog temp) {

    if(temp.getId() == null){
      // 新增
      blogMappper.insert(temp);
    }else {
      // 修改
      blogMappper.updateByPrimaryKeySelective(temp);
    }
  }
}

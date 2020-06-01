package com.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "m_blog")
@Data
public class Blog implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "主键id",required = false)
  private Long id;

  @ApiModelProperty(value = "用户id",required = false)
  @Column(name = "user_id")
  private Long userId;

  @ApiModelProperty(value = "标题",required = false)
  @NotBlank(message = "标题不能为空")
  private String title;

  @ApiModelProperty(value = "摘要",required = false)
  @NotBlank(message = "摘要不能为空")
  private String description;

  @ApiModelProperty(value = "内容",required = false)
  @NotBlank(message = "内容不能为空")
  private String content;

  @ApiModelProperty(value = "本地时间",required = false)
  @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
  private LocalDateTime created;

  @ApiModelProperty(value = "状态",required = false)
  private Integer status;
}

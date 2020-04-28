package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * c测试demo
 *
 * @author lmyou
 * @since 2020-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("A_DEMO")
@KeySequence(value = "T_PATROL_THEME_SEQ", clazz = Long.class)
public class Demo extends Model<Demo> {

  private static final long serialVersionUID = 1L;

  /** 序号 */
  @TableId(value = "ID", type = IdType.INPUT)
  private Long id;
  /** 名称 */
  @TableField("NAME")
  private String name;
  /** 创建人 */
  @TableField("CREATE_BY")
  private Long createby;
  /** 创建时间 */
  @TableField("CREATE_DATE")
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss",timezone="GMT+8")
  private Date createdate;
  /** 修改人 */
  @TableField("UPDATE_BY")
  private Long updateby;
  /** 修改时间 */
  @TableField("UPDATE_DATE")
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss",timezone="GMT+8")
  private Date updatedate;
  /** 逻辑删除标识 */
  @TableField("DEL_FLAG")
  @TableLogic
  private Integer delflag;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}

package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 议案（待）
 *
 * @author YYF
 * @since 2020-06-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_MOTION")
@KeySequence(value = "T_MOTION_SEQ", clazz = Long.class)
public class Motion extends Model<Motion> {

  private static final long serialVersionUID = 1L;

  /** 自增id */
  @TableId(value = "ID", type = IdType.INPUT)
  private Long id;
  /** 项目id */
  @TableField("PROJECT_ID")
  private Long projectId;
  /** 议案名称 */
  @TableField("MOTION_NAME")
  private String motionName;
  /** 议案审批编号 */
  @TableField("MOTION_NO")
  private String motionNo;
  /** 流程会签日期 */
  @TableField("MOTION_DATE")
  private Date motionDate;
  /** 描述 */
  @TableField("DSCP")
  private String dscp;
  /** 创建人 */
  @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
  private Long createBy;
  /** 创建日期 */
  @TableField("CREATE_DATE")
  private Date createDate;
  /** 修改人 */
  @TableField(value = "UPDATE_BY", fill = FieldFill.UPDATE)
  private Long updateBy;
  /** 修改日期 */
  @TableField(value = "UPDATE_DATE", fill = FieldFill.UPDATE)
  private Date updateDate;
  /** 所属公司id */
  @TableField(value = "ORG_ID", fill = FieldFill.INSERT)
  private Long orgId;
  /** 逻辑删除（0:正常 1:删除） */
  @TableField("DEL_FLAG")
  @TableLogic
  private Integer delFlag;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}

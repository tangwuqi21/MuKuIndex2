package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 附件表
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("T_ORDER_ATTACHMENT")
@KeySequence(value = "T_ORDER_ATTACHMENT_SEQ")
public class OrderAttachment extends Model<OrderAttachment> {

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 原文件名称
     */
    @TableField("ORGFILENAME")
    private String orgfilename;
    /**
     * 文件地址
     */
    @TableField("FILEURL")
    private String fileurl;
    /**
     * 描述
     */
    @TableField("DISCRIPTION")
    private String discription;
    /**
     * 创建日期
     */
    @TableField(value="CREATE_DATE", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(value = "UPDATE_DATE", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateDate;
    @TableField(value = "UPDATE_BY", fill = FieldFill.UPDATE)
    private Long updateBy;
    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;
    /**
     * 附件类型 1-合同附件  2-送货记录附件
     */
    @TableField("ATTTYPE")
    private Integer atttype;

    @TableField("PARENT_ID")
    private Long parentId;

    /**
     * 删除标识，0-正常，1-已删除
     */
    @TableField("DEL_FLAG")
    @TableLogic
    private Integer delFlag;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

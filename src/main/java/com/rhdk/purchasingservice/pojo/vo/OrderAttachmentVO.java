package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 附件表
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderAttachmentVO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "序号")
  private Long id;

  @ApiModelProperty(value = "原文件名称")
  private String orgfilename;

  @ApiModelProperty(value = "文件地址")
  private String fileurl;

  @ApiModelProperty(value = "创建日期")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createDate;

  @ApiModelProperty(value = "创建人")
  private Long createBy;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateDate;

  private Long updateBy;

  @ApiModelProperty(value = "附件类型 1-合同附件  2-送货记录附件")
  private Integer atttype;

  @ApiModelProperty(value = "附件所属父级id")
  private Long parentId;

  @ApiModelProperty(value = "删除标识，0-正常，1-已删除")
  private Integer delFlag;
}

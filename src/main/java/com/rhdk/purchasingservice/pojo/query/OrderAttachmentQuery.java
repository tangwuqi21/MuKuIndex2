package com.rhdk.purchasingservice.pojo.query;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * 附件表
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAttachmentQuery extends BaseDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "序号", hidden = true)
  private Long id;

  @ApiModelProperty(value = "原文件名称")
  private String orgfilename;

  @ApiModelProperty(value = "文件地址")
  private String fileurl;

  @ApiModelProperty(value = "附件类型 1-合同附件  2-送货记录附件")
  private Integer atttype;

  @ApiModelProperty(value = "附件所属父级id", hidden = true)
  private Long parentId;
}

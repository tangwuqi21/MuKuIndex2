package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 附件表
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAttachmentDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "序号", hidden = true)
    private Long id;

    @ApiModelProperty(value = "原文件名称")
    private String orgfilename;

    @ApiModelProperty(value = "文件地址")
    private String fileurl;

    @ApiModelProperty(value = "描述", hidden = true)
    private String discription;

    @ApiModelProperty(value = "创建日期", hidden = true)
    private Date createDate;

    @ApiModelProperty(value = "创建人", hidden = true)
    private Long createBy;

    @ApiModelProperty(value = "修改日期", hidden = true)
    private Date updateDate;

    @ApiModelProperty(value = "修改人", hidden = true)
    private Long updateBy;

    @ApiModelProperty(value = "备注", hidden = true)
    private String remark;

    @ApiModelProperty(value = "附件类型 1-合同附件  2-送货记录附件")
    private Integer atttype;

    @ApiModelProperty(value = "附件所属父级id", hidden = true)
    private Long parentId;

    @ApiModelProperty(value = "删除标识，0-正常，1-已删除", hidden = true)
    private Integer delFlag;

}

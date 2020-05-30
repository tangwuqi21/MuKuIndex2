package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetCatVO implements Serializable {
  @ApiModelProperty(value = "ID")
  private Long id;

  /** 父类别id */
  @ApiModelProperty(value = "PID")
  private Long pid;
  /** 层级关系（例:0-1-5） */
  @ApiModelProperty(value = "SEARCH_KEY")
  private String searchKey;

  /** 名称 */
  @ApiModelProperty(value = "NAME")
  private String name;

  /** 描述 */
  @ApiModelProperty(value = "DSCP")
  private String dscp;

  /** 创建人 */
  @ApiModelProperty(value = "CREATE_BY")
  private Long createBy;

  /** 创建日期 */
  @ApiModelProperty(value = "CREATE_DATE")
  private Date createDate;

  /** 修改人 */
  @ApiModelProperty(value = "UPDATE_BY")
  private Long updateBy;

  /** 修改日期 */
  @ApiModelProperty(value = "UPDATE_DATE")
  private Date updateDate;

  /** 所属公司id */
  @ApiModelProperty(value = "ORG_ID")
  private Long orgId;

  /** 逻辑删除（0:正常 1:删除） */
  @ApiModelProperty(value = "DEL_FLAG")
  private Integer delFlag;
  /** 序号 */
  @ApiModelProperty(value = "SORT_NUM")
  private Long sortNum;
}

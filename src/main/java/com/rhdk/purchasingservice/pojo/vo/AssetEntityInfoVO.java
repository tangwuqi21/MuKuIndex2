package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityPrpt;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 合同表
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetEntityInfoVO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID")
  private Long id;

  /** 资产类别id */
  @ApiModelProperty(value = "资产类别id")
  private Long assetCatId;
  /** 资产模板id */
  @ApiModelProperty(value = "资产模板id")
  private Long assetTemplId;
  /** 资产模板版本号 */
  @ApiModelProperty(value = "资产模板版本号")
  private Integer assetTemplVer;
  /** 名称 */
  @ApiModelProperty(value = "名称")
  private String name;
  /** 资产业务编码 */
  @ApiModelProperty(value = "资产业务编码")
  private String itemNo;
  /** 仓库id */
  @ApiModelProperty(value = "仓库id")
  private Long warehouseId;
  /** 采购入库单id（物化时需要） */
  @ApiModelProperty(value = "采购入库单id")
  private Long checkinId;
  /** 数量（量化管理时必需） */
  @ApiModelProperty(value = "数量（量化管理时必需）")
  private Long amount;
  /** 描述 */
  @ApiModelProperty(value = "描述")
  private String dscp;
  /** 创建人 */
  @ApiModelProperty(value = "创建人")
  private Long createBy;
  /** 创建日期 */
  @ApiModelProperty(value = "创建日期")
  private Date createDate;
  /** 修改人 */
  @ApiModelProperty(value = "修改人")
  private Long updateBy;
  /** 修改日期 */
  @ApiModelProperty(value = "修改日期")
  private Date updateDate;
  /** 所属公司id */
  @ApiModelProperty(value = "所属公司id")
  private Long orgId;

  /** 资产标识 （-2：暂存状态，-1:装配草稿状态,0:待签收 1:签收 2:入库 3:消耗 99:虚资产-用于组装） */
  @ApiModelProperty(value = "资产标识")
  private Integer assetStatus;
  /** 逻辑删除（0:正常 1:删除） */
  @ApiModelProperty(value = "逻辑删除")
  private Integer delFlag;

  @ApiModelProperty(value = "资产属性值列表")
  private List<AssetEntityPrpt> assetEntityPrptList;

  @ApiModelProperty(value = "资产明细对象")
  private OrderDelivedetail orderDelivedetail;
}

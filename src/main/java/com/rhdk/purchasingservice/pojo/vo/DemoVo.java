package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 测试
 *
 * @author LMYOU
 * @since 2020-04-27
 */
@Getter
@Setter
public class DemoVo extends BaseDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "序号")
  private Long id;

  @ApiModelProperty(value = "名称")
  private String name;

  @ApiModelProperty(value = "创建人")
  private Long createby;

  @ApiModelProperty(value = "创建时间")
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss",timezone="GMT+8")
  private Date createdate;

  @ApiModelProperty(value = "修改人")
  private Long updateby;

  @ApiModelProperty(value = "修改时间")
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss",timezone="GMT+8")
  private Date updatedate;

  @ApiModelProperty(value = "逻辑删除标识")
  private Integer delflag;

  @ApiModelProperty(value = "修改人名称")
  private String updateuser;

  @ApiModelProperty(value = "创建人名称")
  private String createuser;
}

package com.rhdk.purchasingservice.pojo.query;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 送货明细
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Getter
@Setter
public class OrderDelivedetailQuery extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "中间表id")
    private Long middleId;

    @ApiModelProperty(value = "资产模板id")
    private Long moduleId;
}

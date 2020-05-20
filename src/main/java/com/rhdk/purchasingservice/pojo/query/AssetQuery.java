package com.rhdk.purchasingservice.pojo.query;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetQuery {
    /**
     * 获取送货明细清单参数
     */

    List<Long> assetIds;
    Long assetTemplId;


    /**
     * 获取固有属性值参数
     */
    Long assetTempId;
    String prptIds;
}

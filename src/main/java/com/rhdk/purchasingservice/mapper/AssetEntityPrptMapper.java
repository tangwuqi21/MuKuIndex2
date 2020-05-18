package com.rhdk.purchasingservice.mapper;

import com.rhdk.purchasingservice.pojo.entity.AssetEntityPrpt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 资产属性值 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface AssetEntityPrptMapper extends BaseMapper<AssetEntityPrpt> {
    Integer deleteEntityPrpts(@Param("detailAssetIds") List<Long> detailAssetIds);

    Integer updateEntityprpts(@Param("assetIds") List<Long> assetIds);
}

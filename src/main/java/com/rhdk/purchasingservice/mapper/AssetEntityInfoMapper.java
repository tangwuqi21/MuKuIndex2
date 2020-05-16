package com.rhdk.purchasingservice.mapper;

import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 资产 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface AssetEntityInfoMapper extends BaseMapper<AssetEntityInfo> {
    Integer deleteEntitys(@Param("detailAssetIds") List<Long> detailAssetIds);

    Integer updateEntityInfo(@Param("detailAssetIds") List<Long> detailAssetIds,@Param("dto") OrderDelivemiddleDTO model);
}

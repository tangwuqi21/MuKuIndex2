package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 送货明细 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface OrderDelivedetailMapper extends BaseMapper<OrderDelivedetail> {

  List<Long> getAssetIds(@Param("id") Long id);

  Integer deleteDeliveDetails(@Param("detailAssetIds") List<Long> detailAssetIds);

  Integer updateDetails(
      @Param("detailAssetIds") List<Long> detailAssetIds, @Param("dto") OrderDelivemiddleDTO dto);

  List<Long> getAssetIdsByDId(@Param("middleIds") List<Long> middleIds);

  Integer updateDetailsDel(@Param("assetIds") List<Long> assetIds, @Param("id") Long id);

  Integer updateAssetStatus(@Param("assetQuery") AssetQuery assetQuery);

  List<Map<String, Object>> getEntityIdsByMid(@Param("middleIds") List<Long> middleIds);
}

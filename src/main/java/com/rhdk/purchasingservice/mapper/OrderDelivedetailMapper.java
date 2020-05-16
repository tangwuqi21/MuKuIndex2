package com.rhdk.purchasingservice.mapper;

import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 送货明细 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface OrderDelivedetailMapper extends BaseMapper<OrderDelivedetail> {

    List<Long> getAssetIds(@Param("id") Long id);

    Integer deleteDeliveDetails(@Param("detailAssetIds") List<Long> detailAssetIds);

    Integer updateDetails(@Param("detailAssetIds") List<Long> detailAssetIds,@Param("dto") OrderDelivemiddleDTO dto);
}

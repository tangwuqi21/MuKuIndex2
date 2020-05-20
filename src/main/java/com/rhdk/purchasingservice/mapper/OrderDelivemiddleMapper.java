package com.rhdk.purchasingservice.mapper;

import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 送货记录明细中间表 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
public interface OrderDelivemiddleMapper extends BaseMapper<OrderDelivemiddle> {

        List<Map<String, Object>> getTitleList(@Param("moduleId") Long moduleId);

        List<Map<String, Object>> getTitleMap(@Param("moduleId") Long moduleId);

        Map<String, Object> getContractInfoByMId(@Param("id") Long id);

        List<Long> getMIdsByDeliveryId(@Param("id") Long id);

        List<Integer> getSignStatus(@Param("id") Long id);
}

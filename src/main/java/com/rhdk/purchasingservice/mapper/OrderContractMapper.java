package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import org.apache.ibatis.annotations.Param;

import java.util.Map;


/**
 * <p>
 * 合同表 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface OrderContractMapper extends BaseMapper<OrderContract> {
    Map<String, String> getUserNameByID(@Param("userId") Long userId);
}

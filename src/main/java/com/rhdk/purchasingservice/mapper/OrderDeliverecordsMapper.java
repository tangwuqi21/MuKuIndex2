package com.rhdk.purchasingservice.mapper;

import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 送货单 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface OrderDeliverecordsMapper extends BaseMapper<OrderDeliverecords> {
    List<Long> getIdsBySupplierId(@Param("supplierIds") List<Long> supplierIds);
}

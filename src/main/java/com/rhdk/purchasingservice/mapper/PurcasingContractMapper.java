package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.entity.PurcasingContract;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购合同 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-05-19
 */
public interface PurcasingContractMapper extends BaseMapper<PurcasingContract> {
  List<PurcasingContract> getOrderIds(@Param("dto") List<OrderContractVO> paramStr);
}

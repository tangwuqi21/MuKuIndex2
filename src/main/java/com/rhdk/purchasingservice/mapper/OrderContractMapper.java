package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同表 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface OrderContractMapper extends BaseMapper<OrderContract> {

  OrderContractVO selectContractById(@Param("orderId") Long orderId);

  OrderContractVO selectContractByCId(@Param("id") Long id);

  List<Long> getContractIdList(@Param("contractCompany") String contractCompany);

  void updateContract(
      @Param("id") Long id,
      @Param("contractCompany") String contractCompany,
      @Param("userId") Long userId,
      @Param("orgId") Long orgId);
}

package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
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

  OrderContractVO selectContractById(
      @Param("orderId") Long orderId, @Param("contractId") Long contractId);

  List<Long> getContractIdList(@Param("dto") OrderContractQuery dto);

  void updateContract(
      @Param("id") Long id,
      @Param("contractCompany") String contractCompany,
      @Param("userId") Long userId,
      @Param("orgId") Long orgId);

  IPage<OrderContractVO> selectContractList(
      Page page, @Param("dto") OrderContractQuery dto, @Param("orgId") Long orgId);
}

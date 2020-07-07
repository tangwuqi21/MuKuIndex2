package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.query.ContractCustQuery;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.CustomerInfoVO;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;

import java.util.List;

/**
 * 合同表 服务类
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface IOrderContractService extends IService<OrderContract> {
  IPage<OrderContractVO> searchOrderContractListPage(OrderContractQuery dto, Long orgId);

  ResponseEnvelope searchOrderContractOne(Long id);

  ResponseEnvelope addOrderContract(OrderContractDTO DTO);

  ResponseEnvelope updateOrderContract(OrderContractDTO DTO);

  List<OrderContractVO> getContractInforList(OrderContractQuery dto, Long orgId);

  ResponseEnvelope deleteOrderContract(Long id);

  ResponseEnvelope deleteContractList(List<Long> ids);

  Page<CustomerInfoVO> searchContractCustListPage(ContractCustQuery dto);
}

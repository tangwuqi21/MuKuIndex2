package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;


/**
 * <p>
 * 合同表 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface IOrderContractService extends IService<OrderContract> {
     ResponseEnvelope searchOrderContractListPage(OrderContractDTO DTO);

     ResponseEnvelope searchOrderContractOne(Long id);

     ResponseEnvelope addOrderContract(OrderContractDTO DTO);

     ResponseEnvelope updateOrderContract(OrderContractDTO DTO);

}
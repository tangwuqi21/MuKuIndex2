package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 合同表 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface IOrderContractService extends IService<OrderContract> {
     ResponseEnvelope searchOrderContractListPage(OrderContractQuery DTO);

     ResponseEnvelope searchOrderContractOne(Long id);

     ResponseEnvelope addOrderContract(OrderContractDTO DTO);

     ResponseEnvelope updateOrderContract(OrderContractDTO DTO);

    List<OrderContractVO> getContractInforList(OrderContractDTO dto);

    ResponseEnvelope deleteOrderContract(Long id);
}

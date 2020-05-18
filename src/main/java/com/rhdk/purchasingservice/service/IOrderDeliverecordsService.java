package com.rhdk.purchasingservice.service;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;


/**
 * <p>
 * 送货单 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface IOrderDeliverecordsService extends IService<OrderDeliverecords> {
     ResponseEnvelope searchOrderDeliverecordsListPage(OrderDeliverecordsQuery DTO);

     ResponseEnvelope searchOrderDeliverecordsOne(Long id);

     ResponseEnvelope addOrderDeliverecords(OrderDeliverecordsDTO DTO) throws Exception;

     ResponseEnvelope updateOrderDeliverecords(OrderDeliverecordsDTO DTO) throws Exception;

     ResponseEnvelope deleteOrderDeliverecords(Long id) throws Exception;
}

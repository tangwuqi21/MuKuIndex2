package com.rhdk.purchasingservice.service;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivedetailDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivedetailQuery;


/**
 * <p>
 * 送货明细 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface IOrderDelivedetailService extends IService<OrderDelivedetail> {
     ResponseEnvelope searchOrderDelivedetailListPage(OrderDelivedetailQuery DTO);

     ResponseEnvelope searchOrderDelivedetailOne(Long id);

     ResponseEnvelope addOrderDelivedetail(OrderDelivedetailDTO DTO);

     ResponseEnvelope updateOrderDelivedetail(OrderDelivedetailDTO DTO);

     ResponseEnvelope deleteOrderDelivedetail(Long id);
}

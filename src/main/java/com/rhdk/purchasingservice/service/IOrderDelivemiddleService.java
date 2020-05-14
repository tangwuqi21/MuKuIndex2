package com.rhdk.purchasingservice.service;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;


/**
 * <p>
 * 送货记录明细中间表 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
public interface IOrderDelivemiddleService extends IService<OrderDelivemiddle> {
     ResponseEnvelope searchOrderDelivemiddleListPage(OrderDelivemiddleQuery DTO);

     ResponseEnvelope searchOrderDelivemiddleOne(Long id);

     ResponseEnvelope addOrderDelivemiddle(OrderDelivemiddleDTO DTO);

     ResponseEnvelope updateOrderDelivemiddle(OrderDelivemiddleDTO DTO);

     ResponseEnvelope deleteOrderDelivemiddle(Long id);
}

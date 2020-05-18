package com.rhdk.purchasingservice.service;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


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

     ResponseEnvelope addOrderDelivemiddle(OrderDelivemiddleDTO DTO) throws Exception;

     List<Map<String, Object>> getTitleList(Long moduleId);

     List<Map<String, Object>> getTitleMap(Long moduleId);

     Integer deleteByPassNo(Long id);

     ResponseEnvelope deleteOrderDetailrecords(Long id);

    ResponseEnvelope updateOrderMiddle(OrderDelivemiddleDTO dto);
}

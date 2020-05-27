package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.rhdk.purchasingservice.pojo.query.OrderDelivedetailQuery;

/**
 * 送货明细 服务类
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface IOrderDelivedetailService extends IService<OrderDelivedetail> {
  ResponseEnvelope searchOrderDelivedetailListPage(OrderDelivedetailQuery DTO);
}

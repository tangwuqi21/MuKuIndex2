package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.common.config.FeignExecptionConfig;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author: LMYOU
 * @create: 2020-04-28 @Description:经租系统，库存管理服务
 */
@FeignClient(value = "${feignName.inventoryService}", fallback = FeignExecptionConfig.class)
@Component
public interface InventoryServiceFeign {
  /**
   * 获取明细列表是否有暂存的签收记录
   *
   * @param middleIds
   * @param token
   * @return
   */
  @RequestMapping(value = "/inventory/receive/checkReceiveIsExist", method = RequestMethod.POST)
  ResponseEnvelope<Map<String, Object>> checkReceiveIsExist(
      @RequestParam("middleIds") Long[] middleIds,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 送货明细删除，通知签收模块暂存的数据清除
   *
   * @param id
   * @param token
   * @return
   */
  @RequestMapping(value = "/inventory/receive/deleteReceiveOne", method = RequestMethod.POST)
  ResponseEnvelope<Map<String, Object>> deleteReceiveOne(
      @RequestParam("id") Integer id, @RequestHeader(value = "Authorization") String token);
}

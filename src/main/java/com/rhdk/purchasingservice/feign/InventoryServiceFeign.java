package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.common.config.FeignExecptionConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: LMYOU
 * @create: 2020-04-28
 * @Description:经租系统，库存管理服务
 */
@FeignClient(value = "${feignName.inventoryService}", fallback = FeignExecptionConfig.class)
@Component
public interface InventoryServiceFeign {

}

package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.feign.fallBack.DemoHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: YYF
 * @create: 2020-04-28
 * @Description:
 */
@FeignClient(value = "PATROLSERVICE-LOCAL",fallback = DemoHystrix.class)
@Component
public interface IDemoFeign {



}

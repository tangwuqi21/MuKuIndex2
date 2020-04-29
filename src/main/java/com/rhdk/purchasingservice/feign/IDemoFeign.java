package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.fallBack.DemoHystrix;
import com.rhdk.purchasingservice.pojo.vo.DemoVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author: LMYOU
 * @create: 2020-04-28
 * @Description:
 */
@FeignClient(value = "INVENTORY-SERVICE-DEV",fallback = DemoHystrix.class)
@Component
public interface IDemoFeign {
    @RequestMapping(value="/asset/demo/searchDemoListPage",method= RequestMethod.POST)
    ResponseEnvelope searchDemoListPage(@RequestBody DemoVo dto, @RequestHeader(value = "Authorization") String token);
}

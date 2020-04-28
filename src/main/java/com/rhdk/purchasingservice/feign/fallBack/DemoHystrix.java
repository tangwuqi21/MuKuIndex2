package com.rhdk.purchasingservice.feign.fallBack;

import com.rhdk.purchasingservice.feign.IDemoFeign;
import com.rhdk.purchasingservice.pojo.entity.Demo;
import org.springframework.stereotype.Component;

/**
 * @author: YYF
 * @create: 2020-04-28
 * @Description:
 */
@Component
public class DemoHystrix implements IDemoFeign {
}

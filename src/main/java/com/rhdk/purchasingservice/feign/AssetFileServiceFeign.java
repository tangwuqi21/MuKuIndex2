package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.common.config.FeignExecptionConfig;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityInfo;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityPrpt;
import com.rhdk.purchasingservice.pojo.entity.AssetTmplInfo;
import com.rhdk.purchasingservice.pojo.entity.Customer;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: LMYOU
 * @create: 2020-04-28
 * @Description:经租系统，资产基础服务
 */
@FeignClient(value = "${feignName.assetService}", fallback = FeignExecptionConfig.class,configuration = AssetFileServiceFeign.MultipartSupportConfig.class)
@Component
public interface AssetFileServiceFeign {

    /**
     * 跨服务调用的方法,注意MultipartFile的注解要用@RequestPart
     */
    @RequestMapping(value = "/fileUploadService/uploadSingleFile", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadSingleFile(@RequestPart("file") MultipartFile file, @RequestHeader(value = "Authorization") String token);


    /**
     * 引用配置类MultipartSupportConfig.并且实例化
     */
    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }
}

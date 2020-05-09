package com.rhdk.purchasingservice.common.config;


import com.alibaba.fastjson.JSONObject;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@Configuration
public class FeignExecptionConfig  {
  @Bean
  public ErrorDecoder errorDecoder() {
    return new UserErrorDecoder();
  }
  /**
   * 重新实现feign的异常处理，捕捉restful接口返回的json格式的异常信息
   *
   */
  public class UserErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
      Exception exception = null;
      ObjectMapper mapper = new ObjectMapper();
      //空属性处理
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
      //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
      mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      //禁止使用int代表enum的order来反序列化enum
      mapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
      try {
        String json = Util.toString(response.body().asReader());
        exception = new RuntimeException(json);
        if (StringUtils.isEmpty(json)) {
          return null;
        }
        JSONObject result = mapper.readValue(json, JSONObject.class);
        // 业务异常包装成自定义异常类MyException
        if (result.getInteger("status") != HttpStatus.OK.value()) {
          exception = new RuntimeException(json);
        }
      } catch (IOException ex) {
          log.error(ex.getMessage(), ex);
      }
      return exception;
    }
  }

}

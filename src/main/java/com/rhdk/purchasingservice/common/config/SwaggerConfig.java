package com.rhdk.purchasingservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zh @ClassName cn.saytime.Swgger2 @Description
 * @date 2017-07-10 22:12:31
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public Docket createRestApi() {
    // 添加head参数配置start
    ParameterBuilder tokenPar = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<>();
    tokenPar
        .name("Authorization")
        .description("令牌")
        .modelRef(new ModelRef("string"))
        .parameterType("header")
        .required(false)
        .build();
    pars.add(tokenPar.build());
    // 添加head参数配置end
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.rhdk.purchasingservice.controller"))
        .paths(PathSelectors.any())
        .build()
        .globalOperationParameters(pars); // 注意这里
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("融和电科经租系统api文档")
        .description("融和电科经租系统V1_2019，https://test.pm.private.igen-tech.com:8443/rhzl")
        .termsOfServiceUrl("https://test.pm.private.igen-tech.com:8443/rhzl")
        .version("0.1")
        .build();
  }
}

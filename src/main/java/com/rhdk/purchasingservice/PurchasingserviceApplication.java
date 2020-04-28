package com.rhdk.purchasingservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;


@SpringBootApplication
@ComponentScan(basePackages = {"com.rhdk"})
@MapperScan("com.rhdk.purchasingservice.mapper")
@EnableEurekaClient
@EnableAspectJAutoProxy
@ServletComponentScan
@EnableSwagger2
@EnableFeignClients
public class PurchasingserviceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PurchasingserviceApplication.class, args);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 允许上传的文件最大值
        factory.setMaxFileSize("500MB"); // KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("500MB");
        return factory.createMultipartConfig();
    }

    @Override
    public void run(String... args) throws Exception {}
}

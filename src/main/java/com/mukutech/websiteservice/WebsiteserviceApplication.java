package com.mukutech.websiteservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mukutech"})
@MapperScan("com.mukutech.websiteservice.mapper")
@EnableAspectJAutoProxy
@ServletComponentScan
@EnableSwagger2
public class WebsiteserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteserviceApplication.class, args);
    }
}

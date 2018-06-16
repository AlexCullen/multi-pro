package com.douby.solrweb.swagger;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @Author: cpzh
 * @Date: 2018/5/18 13:59
 * TODO:
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket SwaggerSpringfoxDocket() {
        StopWatch watch = new StopWatch();
        watch.start();
        springfox.documentation.service.ApiInfo apiInfo = new ApiInfoBuilder().title("ssm商城系统")
                .description("技术栈: Spring + springMvc + MyBatis + Dubbo + redis+ swagger2")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .genericModelSubstitutes(ResponseEntity.class)
                .forCodeGeneration(true)
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(ZonedDateTime.class, Date.class)
                .directModelSubstitute(LocalDateTime.class, Date.class)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .apis(RequestHandlerSelectors.basePackage("com.douby.common"))
                .build();
        watch.stop();
        return docket;
    }
}

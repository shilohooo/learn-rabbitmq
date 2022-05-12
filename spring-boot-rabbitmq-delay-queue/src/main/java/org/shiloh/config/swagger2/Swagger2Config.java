package org.shiloh.config.swagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/**
 * Swagger2 配置
 *
 * @author shiloh
 * @date 2022/5/12 22:53
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    /**
     * Swagger2 相关配置
     *
     * @author shiloh
     * @date 2022/5/12 22:53
     */
    @Bean
    public Docket webApi() {
        final ApiInfo webApiInfo = this.getWebApiInfo();
        return new Docket(SWAGGER_2)
                .groupName("WebApi")
                .apiInfo(webApiInfo)
                .select()
                .build();
    }

    /**
     * 获取 Swagger2 API 配置信息
     *
     * @author shiloh
     * @date 2022/5/12 22:54
     */
    private ApiInfo getWebApiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBoot 集成 RabbitMq 接口文档")
                .version("1.0.0")
                .description("SpringBoot 集成 RabbitMq 接口文档")
                .contact(new Contact("shiloh", "https://shiloh595.site", "lixiaolei595@gmail.com"))
                .build();
    }
}

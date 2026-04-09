package com.example.ordermanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderManagementOpenApi() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Order Management Service API")
                        .version("1.0.0")
                        .description("Order lifecycle management microservice"));
    }
}
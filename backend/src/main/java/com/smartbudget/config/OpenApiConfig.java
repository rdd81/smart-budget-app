package com.smartbudget.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartBudgetOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Budget App API")
                        .description("Personal Finance Management Application")
                        .version("v1.0"));
    }
}

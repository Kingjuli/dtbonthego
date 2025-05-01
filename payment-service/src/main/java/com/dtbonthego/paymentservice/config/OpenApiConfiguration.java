package com.dtbonthego.paymentservice.config;

import com.dtbonthego.common.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation in the Payment Service.
 */
@Configuration
public class OpenApiConfiguration extends OpenApiConfig {

    /**
     * Configures OpenAPI for the Payment Service using the common configuration.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return super.customOpenAPI(
                "Payment Service API",
                "API for managing payment transactions and processing",
                "1.0");
    }
}

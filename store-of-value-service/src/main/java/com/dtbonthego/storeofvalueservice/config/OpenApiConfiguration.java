package com.dtbonthego.storeofvalueservice.config;

import com.dtbonthego.common.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation in the Store of Value Service.
 */
@Configuration
public class OpenApiConfiguration extends OpenApiConfig {

    /**
     * Configures OpenAPI for the Store of Value Service using the common configuration.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return super.customOpenAPI(
                "Store of Value Service API",
                "API for managing bank accounts and balances",
                "1.0");
    }
}

package com.dtbonthego.eventsservice.config;

import com.dtbonthego.common.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation in the Events Service.
 */
@Configuration
public class OpenApiConfiguration extends OpenApiConfig {

    /**
     * Configures OpenAPI for the Events Service using the common configuration.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return super.customOpenAPI(
                "Events Service API",
                "API for managing notifications and events for customer transactions",
                "1.0");
    }
}

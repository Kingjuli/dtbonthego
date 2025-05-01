package com.dtbonthego.profileservice.config;

import com.dtbonthego.common.config.OpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation in the Profile Service.
 */
@Configuration
public class OpenApiConfiguration extends OpenApiConfig {

    /**
     * Configures OpenAPI for the Profile Service using the common configuration.
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return super.customOpenAPI(
                "Profile Service API",
                "API for managing Customer profiles, authentication, and authorization",
                "1.0");
    }
}

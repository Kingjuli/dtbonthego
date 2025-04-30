package com.dtbonthego.profileservice.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Sets up the API documentation and security scheme.
 */
@Configuration
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI documentation.
     * Includes API information, contact details, and security requirements.
     * 
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("DTB On The Go Profile Service API")
                        .description("API for managing customer profiles, authentication, and authorization")
                        .version("1.0")
                        .contact(new Contact()
                                .name("DTB On The Go")
                                .email("juliusmuruthi@gmail.com")
                                .url("https://github.com/kingjuli/dtbonthego"))
                        .license(new License()
                                .name("DTB License")
                                .url("https://github.com/kingjuli/dtbonthego/licenses"))
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
} 
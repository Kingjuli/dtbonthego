package com.dtbonthego.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
/**
 * Common OpenAPI configuration that can be used across all microservices.
 */
public class OpenApiConfig {

        /**
         * Configures OpenAPI with common settings and JWT authentication.
         *
         * @param title       the API title
         * @param description the API description
         * @param version     the API version
         * @return the configured OpenAPI instance
         */
        @Bean
        public OpenAPI customOpenAPI(String title, String description, String version) {
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                .info(new Info()
                                                .title(title)
                                                .version(version)
                                                .description(description)
                                                .contact(new Contact()
                                                                .name("DTB On The Go")
                                                                .email("juliusmuruthi@gmail.com")
                                                                .url("https://github.com/kingjuli/dtbonthego"))
                                                .license(new License()
                                                                .name("DTB License")
                                                                .url("https://github.com/kingjuli/dtbonthego/licenses")))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components().addSecuritySchemes(securitySchemeName,
                                                new SecurityScheme()
                                                                .name(securitySchemeName)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")));
        }
}

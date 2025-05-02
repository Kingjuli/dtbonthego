package com.dtbonthego.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;

/**
 * Common OpenAPI configuration that can be used across all microservices.
 */
public class OpenApiConfig {

    /**
     * Configures OpenAPI with common settings and JWT authentication.
     *
     * @param title the API title
     * @param description the API description
     * @param version the API version
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI(String title, String description, String version) {
        
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
                                .url("https://github.com/kingjuli/dtbonthego/licenses")));
    }
}

package com.dtbonthego.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

/**
 * Handles user profile management, authentication, and authorization.
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Profile Service API",
        version = "1.0",
        description = "API for managing Customer profiles, authentication, and authorization",
        contact = @Contact(
            name = "DTB On The Go",
            email = "juliusmuruthi@gmail.com",
            url = "https://github.com/kingjuli/dtbonthego"
        ),
        license = @License(
            name = "DTB License",
            url = "https://github.com/kingjuli/dtbonthego/licenses"
        )
    )
)
public class ProfileServiceApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProfileServiceApplication.class, args);
    }
} 
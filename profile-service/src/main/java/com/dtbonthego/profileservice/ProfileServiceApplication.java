package com.dtbonthego.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Handles user profile management, authentication, and authorization.
 */
@SpringBootApplication
@ComponentScan("com.dtbonthego")
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
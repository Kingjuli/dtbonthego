package com.dtbonthego.eventsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Events Service Application - Handles notifications and events for customer transactions.
 * Integrates with the Payment Service using a message-based approach.
 */
@SpringBootApplication
@ComponentScan("com.dtbonthego")
public class EventsServiceApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EventsServiceApplication.class, args);
    }
}

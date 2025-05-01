package com.dtbonthego.storeofvalueservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Store of Value Service application.
 * It handles bank accounts management and balances.
 */

@SpringBootApplication
@ComponentScan("com.dtbonthego")
public class StoreOfValueServiceApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(StoreOfValueServiceApplication.class, args);
    }
}
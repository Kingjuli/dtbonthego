package com.dtbonthego.paymentservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration class.
 */
@Configuration
@EnableRetry
public class AppConfiguration {

    /**
     * Configure a RestTemplate bean for HTTP calls
     * @param builder
     * @return 
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}

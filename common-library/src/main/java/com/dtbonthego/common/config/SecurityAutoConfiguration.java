package com.dtbonthego.common.config;

import com.dtbonthego.common.security.WebSecurityConfigBase;
import com.dtbonthego.common.security.jwt.AuthEntryPointJwt;
import com.dtbonthego.common.security.jwt.AuthTokenFilter;
import com.dtbonthego.common.security.jwt.JwtUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for security components.
 * This class automatically configures security beans for all microservices.
 */
@AutoConfiguration
@Import(WebSecurityConfigBase.class)
public class SecurityAutoConfiguration {

    /**
     * Creates the JWT authentication entry point if not already defined.
     *
     * @return the JWT authentication entry point
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthEntryPointJwt authEntryPointJwt() {
        return new AuthEntryPointJwt();
    }

    /**
     * Creates the JWT authentication filter if not already defined.
     *
     * @return the JWT authentication filter
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Creates the JWT utility if not already defined.
     *
     * @return the JWT utility
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }
}

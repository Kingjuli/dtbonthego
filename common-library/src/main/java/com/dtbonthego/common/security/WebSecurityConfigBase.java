package com.dtbonthego.common.security;

import com.dtbonthego.common.security.jwt.AuthEntryPointJwt;
import com.dtbonthego.common.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Base security configuration class for all microservices.
 * Provides common security settings and JWT authentication.
 */
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfigBase {
    
    @Autowired
    protected AuthEntryPointJwt unauthorizedHandler;
    
    @Autowired
    protected AuthTokenFilter authTokenFilter;
    
    @Autowired(required = false)
    protected AuthenticationProvider authenticationProvider;
    
    /**
     * Configures the security filter chain with common settings.
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth
                    // Common API documentation endpoints
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    
                    // Monitoring endpoints
                    .requestMatchers("/actuator/**").permitAll()
                    
                    // Profile service endpoints
                    .requestMatchers("/auth/**").permitAll()
                    
                    // Payment service endpoints
                    .requestMatchers("/webhooks/**").permitAll()
                    
                    // Require authentication for all other endpoints
                    .anyRequest().authenticated()
            );
        
        // Add JWT filter
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Add authentication provider if available
        if (authenticationProvider != null) {
            http.authenticationProvider(authenticationProvider);
        }
        
        return http.build();
    }
    
    /**
     * Configures CORS for the application.
     *
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

package com.dtbonthego.profileservice.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for handling login requests.
 * Contains the credentials needed for authentication.
 */
@Data
public class LoginRequest {
    /**
     * Username for authentication, cannot be blank
     */
    @NotBlank
    private String username;

    /**
     * Password for authentication, cannot be blank
     */
    @NotBlank
    private String password;
} 
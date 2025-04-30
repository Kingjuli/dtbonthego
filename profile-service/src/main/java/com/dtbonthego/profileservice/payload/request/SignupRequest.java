package com.dtbonthego.profileservice.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * Data Transfer Object for handling user registration requests.
 * Contains all necessary information to create a new user account.
 */
@Data
public class SignupRequest {
    /**
     * User's first name, 3-50 characters, required
     */
    @NotBlank
    @Size(min = 3, max = 50)
    private String firstName;

    /**
     * User's last name, 3-50 characters, required
     */
    @NotBlank
    @Size(min = 3, max = 50)
    private String lastName;

    /**
     * Username for authentication, 3-20 characters, required
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    /**
     * Email address, must be valid format and required
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * Password, 6-40 characters, required
     */
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    /**
     * Optional phone number, up to 15 characters
     */
    @Size(max = 15)
    private String phoneNumber;

    /**
     * Optional set of roles to assign to the user
     * If not provided, default role (ROLE_CUSTOMER) will be assigned
     */
    private Set<String> roles;
} 
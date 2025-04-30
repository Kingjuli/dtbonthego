package com.dtbonthego.profileservice.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for handling user profile update requests.
 * Contains fields that can be updated in a user's profile.
 * Note: Password updates are handled through a separate endpoint.
 */
@Data
public class UpdateProfileRequest {
    /**
     * User's first name, 3-50 characters
     * If null, the field won't be updated
     */
    @Size(min = 3, max = 50)
    private String firstName;

    /**
     * User's last name, 3-50 characters
     * If null, the field won't be updated
     */
    @Size(min = 3, max = 50)
    private String lastName;

    /**
     * Email address, must be valid format
     * If null, the field won't be updated
     */
    @Size(max = 50)
    @Email
    private String email;
    
    /**
     * Phone number, up to 15 characters
     * If null, the field won't be updated
     */
    @Size(max = 15)
    private String phoneNumber;
} 
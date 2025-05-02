package com.dtbonthego.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a user profile from the Profile Service.
 * Contains only essential fields needed for notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    
    /**
     * The ID of the profile
     */
    private Long id;
    
    /**
     * The email of the profile
     */
    private String email;
    
    /**
     * The first name of the profile
     */
    private String firstName;
    
    /**
     * The last name of the profile
     */
    private String lastName;
    
    /**
     * The phone number of the profile
     */
    private String phoneNumber;
} 
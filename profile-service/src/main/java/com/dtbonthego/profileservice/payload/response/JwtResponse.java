package com.dtbonthego.profileservice.payload.response;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for the response sent after successful authentication.
 * Contains the JWT token and basic user information.
 */
@Data
public class JwtResponse {
    /**
     * JWT token for authentication
     */
    private String token;
    
    /**
     * Token type, always "Bearer"
     */
    private String type = "Bearer";
    
    /**
     * User ID
     */
    private Long id;
    
    /**
     * Username
     */
    private String username;
    
    /**
     * User's email address
     */
    private String email;
    
    /**
     * List of roles assigned to the user
     */
    private List<String> roles;

    /**
     * Constructor for creating a full JWT response
     * 
     * @param accessToken the JWT token
     * @param id the user's ID
     * @param username the username
     * @param email the user's email
     * @param roles the user's roles
     */
    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
} 
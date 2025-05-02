package com.dtbonthego.paymentservice.service;

import com.dtbonthego.paymentservice.model.dto.ProfileDTO;
import org.springframework.stereotype.Service;

/**
 * Mock client service for profile operations in tests.
 * This is a test-only class to match what's referenced in the TransactionServiceImplTest class.
 */
@Service
public class ProfileServiceClient {
    
    /**
     * Mock implementation for tests.
     * @param token JWT token
     * @return ProfileDTO object for testing
     */
    public ProfileDTO getMyProfile(String token) {
        return ProfileDTO.builder()
            .id(101L)
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .phoneNumber("1234567890")
            .build();
    }
} 
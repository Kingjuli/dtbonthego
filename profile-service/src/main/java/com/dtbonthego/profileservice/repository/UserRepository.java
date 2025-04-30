package com.dtbonthego.profileservice.repository;

import com.dtbonthego.profileservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing User entities.
 * Provides methods for CRUD operations and custom queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by their username
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by their email address
     * 
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a username already exists
     * 
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    Boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists
     * 
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    Boolean existsByEmail(String email);
} 
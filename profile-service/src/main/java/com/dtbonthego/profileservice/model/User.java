package com.dtbonthego.profileservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user in the system.
 * Contains personal information, authentication details, and role assignments.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's first name, required
     */
    @NotBlank
    @Size(max = 50)
    private String firstName;

    /**
     * User's last name, required
     */
    @NotBlank
    @Size(max = 50)
    private String lastName;

    /**
     * Unique username for authentication
     */
    @NotBlank
    @Size(max = 20)
    private String username;

    /**
     * User's email address, must be unique
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * User's password, stored encrypted
     */
    @NotBlank
    @Size(max = 120)
    private String password;

    /**
     * Optional contact phone number
     */
    @Size(max = 15)
    private String phoneNumber;

    /**
     * Flag indicating if the account is enabled
     */
    private boolean enabled;
    
    /**
     * Timestamp of when the account was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp of when the account was last updated
     */
    private LocalDateTime updatedAt;

    /**
     * Roles assigned to this user, determining permissions
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Sets creation and update timestamps upon entity creation
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the update timestamp upon entity modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
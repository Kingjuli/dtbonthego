package com.dtbonthego.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a role in the system.
 * Roles are used for role-based access control (RBAC).
 * Each user can have multiple roles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * The name of the role, using the ERole enum
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
} 
package com.dtbonthego.profileservice.repository;

import com.dtbonthego.profileservice.model.ERole;
import com.dtbonthego.profileservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Role entities.
 * Provides methods for CRUD operations and finding roles by name.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Find a role by its name (enum value)
     * 
     * @param name the role name to search for
     * @return an Optional containing the role if found
     */
    Optional<Role> findByName(ERole name);
} 
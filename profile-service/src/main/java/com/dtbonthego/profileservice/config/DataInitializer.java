package com.dtbonthego.profileservice.config;

import com.dtbonthego.profileservice.model.ERole;
import com.dtbonthego.profileservice.model.Role;
import com.dtbonthego.profileservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Component that initializes required data in the database.
 * Runs when the application starts up to ensure necessary data exists.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Creates all necessary roles in the database if they don't already exist.
     * Currently creates ROLE_ADMIN and ROLE_CUSTOMER roles.
     * 
     * @param args command line arguments (not used)
     */
    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        Arrays.stream(ERole.values()).forEach(role -> {
            if (roleRepository.findByName(role).isEmpty()) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        });
    }
} 
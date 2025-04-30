package com.dtbonthego.profileservice.controller;

import com.dtbonthego.profileservice.model.User;
import com.dtbonthego.profileservice.payload.request.ChangePasswordRequest;
import com.dtbonthego.profileservice.payload.request.UpdateProfileRequest;
import com.dtbonthego.profileservice.payload.response.MessageResponse;
import com.dtbonthego.profileservice.repository.UserRepository;
import com.dtbonthego.profileservice.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that provides endpoints for viewing and updating profile information when authenticated.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/profile")
@Tag(name = "Profile", description = "User Profile management API")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieves the profile information of the currently authenticated user.
     * 
     * @return ResponseEntity containing the user's profile data
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get the current user's profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Remove sensitive information before returning
        user.setPassword(null);
        
        return ResponseEntity.ok(user);
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * Only updates fields that are provided in the request.
     * 
     * @param updateRequest the profile information to update
     * @return ResponseEntity with success message or error
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Update the current user's profile (name, email, phone only)")
    public ResponseEntity<?> updateCurrentUserProfile(@Valid @RequestBody UpdateProfileRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Update user information if provided
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        
        if (updateRequest.getEmail() != null && 
                !user.getEmail().equals(updateRequest.getEmail()) && 
                userRepository.existsByEmail(updateRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        } else if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("User profile updated successfully!"));
    }
    
    /**
     * Changes the password of the currently authenticated user.
     * Requires verification of the current password before allowing the change.
     * 
     * @param passwordRequest containing the current and new passwords
     * @return ResponseEntity with success message
     * @throws BadCredentialsException if the current password is incorrect
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Change the current user's password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest passwordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Verify the current password
        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Error: Current password is incorrect!");
        }
        
        // Update with the new password
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    /**
     * Retrieves the profile information of a user by ID.
     * Only accessible to users with ADMIN role.
     * 
     * @param id the ID of the user to retrieve
     * @return ResponseEntity containing the user's profile data
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user profile by ID (Admin only)")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        // Remove sensitive information before returning
        user.setPassword(null);
        
        return ResponseEntity.ok(user);
    }

    /**
     * Deletes a user by ID.
     * Only accessible to users with ADMIN role.
     * 
     * @param id the ID of the user to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user by ID (Admin only)")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        userRepository.delete(user);
        
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }
} 
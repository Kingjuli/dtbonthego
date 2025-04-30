package com.dtbonthego.profileservice.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for handling password change requests.
 * Requires both the current password (for verification) and the new password.
 */
@Data
public class ChangePasswordRequest {
    /**
     * Current password for verification, cannot be blank
     */
    @NotBlank
    private String currentPassword;

    /**
     * New password to set, 6-40 characters, cannot be blank
     */
    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;
} 
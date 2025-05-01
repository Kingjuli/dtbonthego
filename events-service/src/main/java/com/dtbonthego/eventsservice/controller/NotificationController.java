package com.dtbonthego.eventsservice.controller;

import com.dtbonthego.eventsservice.model.dto.NotificationDTO;
import com.dtbonthego.eventsservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for notification management.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "API for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications for the authenticated user.
     *
     * @return list of notifications
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get all notifications for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        Long profileId = getAuthenticatedProfileId();
        log.info("Getting notifications for authenticated user with profile ID: {}", profileId);
        List<NotificationDTO> notifications = notificationService.getNotificationsByProfileId(profileId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get a notification by ID for the authenticated user.
     *
     * @param id the notification ID
     * @return the notification if found and belongs to the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get a notification by ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notification",
                    content = @Content(schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<NotificationDTO> getNotificationById(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable Long id) {
        log.info("Getting notification with ID: {}", id);
        NotificationDTO notification = notificationService.getNotificationById(id);
        
        // Verify the notification belongs to the authenticated user
        Long profileId = getAuthenticatedProfileId();
        if (!notification.getProfileId().equals(profileId) && !isAdmin()) {
            log.warn("User with profile ID {} attempted to access notification {} belonging to profile {}",
                    profileId, id, notification.getProfileId());
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(notification);
    }

    /**
     * Get all notifications for a specific transaction.
     *
     * @param transactionId the transaction ID
     * @return list of notifications
     */
    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get all notifications for a specific transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<NotificationDTO>> getNotificationsByTransactionId(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable Long transactionId) {
        log.info("Getting notifications for transaction ID: {}", transactionId);
        List<NotificationDTO> notifications = notificationService.getNotificationsByTransactionId(transactionId);
        
        // Filter notifications to only show those belonging to the authenticated user
        Long profileId = getAuthenticatedProfileId();
        if (!isAdmin()) {
            notifications = notifications.stream()
                    .filter(n -> n.getProfileId().equals(profileId))
                    .toList();
        }
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Gets the profile ID of the authenticated user.
     *
     * @return the authenticated user's profile ID
     */
    private Long getAuthenticatedProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getPrincipal().toString());
    }

    /**
     * Checks if the authenticated user has the ADMIN role.
     *
     * @return true if the user is an admin, false otherwise
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

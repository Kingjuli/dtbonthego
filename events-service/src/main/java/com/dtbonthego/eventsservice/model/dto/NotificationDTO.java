package com.dtbonthego.eventsservice.model.dto;

import com.dtbonthego.eventsservice.model.NotificationStatus;
import com.dtbonthego.eventsservice.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Notification entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long profileId;
    private NotificationType type;
    private String subject;
    private String content;
    private String recipient;
    private NotificationStatus status;
    private Long transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
}

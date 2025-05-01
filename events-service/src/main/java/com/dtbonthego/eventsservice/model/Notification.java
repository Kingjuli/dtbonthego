package com.dtbonthego.eventsservice.model;

import com.dtbonthego.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a notification in the system.
 * Stores details about notifications sent to customers.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    /**
     * The ID of the notification
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the profile/user this notification was sent to
     */
    @Column(nullable = false)
    private Long profileId;
    
    /**
     * The type of notification (EMAIL, SMS)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    /**
     * The subject or title of the notification
     */
    @Column(nullable = false)
    private String subject;
    
    /**
     * The content/body of the notification
     */
    @Column(nullable = false, length = 1000)
    private String content;
    
    /**
     * The recipient address (email or phone number)
     */
    @Column(nullable = false)
    private String recipient;
    
    /**
     * The status of the notification (PENDING, SENT, FAILED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
    
    /**
     * The ID of the transaction that triggered this notification
     */
    private Long transactionId;
    
    /**
     * When the notification was sent (or attempted to be sent)
     */
    private LocalDateTime sentAt;
    
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
    }
}

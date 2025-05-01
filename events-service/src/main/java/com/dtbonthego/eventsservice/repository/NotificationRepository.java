package com.dtbonthego.eventsservice.repository;

import com.dtbonthego.eventsservice.model.Notification;
import com.dtbonthego.eventsservice.model.NotificationStatus;
import com.dtbonthego.eventsservice.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing and manipulating Notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific profile.
     * 
     * @param profileId the ID of the profile
     * @return list of notifications
     */
    List<Notification> findByProfileId(Long profileId);
    
    /**
     * Find all notifications related to a specific transaction.
     * 
     * @param transactionId the ID of the transaction
     * @return list of notifications
     */
    List<Notification> findByTransactionId(Long transactionId);
    
    /**
     * Find all notifications with a specific status.
     * 
     * @param status the notification status
     * @return list of notifications
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * Find all notifications of a specific type.
     * 
     * @param type the notification type
     * @return list of notifications
     */
    List<Notification> findByType(NotificationType type);
    
    /**
     * Find all notifications for a specific profile with a specific status.
     * 
     * @param profileId the ID of the profile
     * @param status the notification status
     * @return list of notifications
     */
    List<Notification> findByProfileIdAndStatus(Long profileId, NotificationStatus status);
}

package com.dtbonthego.eventsservice.service;

import com.dtbonthego.eventsservice.model.dto.NotificationDTO;
import com.dtbonthego.eventsservice.model.dto.TransactionEvent;

import java.util.List;

/**
 * Service interface for managing notifications.
 */
public interface NotificationService {
    
    /**
     * Process a transaction event and create appropriate notifications.
     * 
     * @param event the transaction event to process
     * @return list of created notifications
     */
    List<NotificationDTO> processTransactionEvent(TransactionEvent event);
    
    /**
     * Get all notifications for a specific profile.
     * 
     * @param profileId the ID of the profile
     * @return list of notifications
     */
    List<NotificationDTO> getNotificationsByProfileId(Long profileId);
    
    /**
     * Get a notification by its ID.
     * 
     * @param id the notification ID
     * @return the notification if found
     */
    NotificationDTO getNotificationById(Long id);
    
    /**
     * Get all notifications related to a specific transaction.
     * 
     * @param transactionId the ID of the transaction
     * @return list of notifications
     */
    List<NotificationDTO> getNotificationsByTransactionId(Long transactionId);
}

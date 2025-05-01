package com.dtbonthego.eventsservice.model;

/**
 * Enumeration of notification statuses in the system.
 */
public enum NotificationStatus {
    /**
     * Notification is pending to be sent
     */
    PENDING,
    
    /**
     * Notification has been successfully sent
     */
    SENT,
    
    /**
     * Notification sending failed
     */
    FAILED
}

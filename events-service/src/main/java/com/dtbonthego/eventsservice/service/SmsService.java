package com.dtbonthego.eventsservice.service;

/**
 * Service interface for sending SMS notifications.
 */
public interface SmsService {
    
    /**
     * Send an SMS notification.
     * 
     * @param to the recipient phone number
     * @param message the SMS message content
     * @return true if the SMS was sent successfully, false otherwise
     */
    boolean sendSms(String to, String message);
}

package com.dtbonthego.eventsservice.service;

/**
 * Service interface for sending email notifications.
 */
public interface EmailService {
    
    /**
     * Send an email notification.
     * 
     * @param to the recipient email address
     * @param subject the email subject
     * @param content the email content/body
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(String to, String subject, String content);
}

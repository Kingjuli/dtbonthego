package com.dtbonthego.eventsservice.service.impl;

import com.dtbonthego.eventsservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the EmailService interface.
 * This is a mock implementation that logs the email details instead of actually sending emails.
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${notification.email.enabled}")
    private boolean emailEnabled;

    @Value("${notification.email.from}")
    private String fromEmail;

    /**
     * {@inheritDoc}
     * This is a mock implementation that logs the email details instead of actually sending emails.
     */
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        if (!emailEnabled) {
            log.info("Email notifications are disabled");
            return false;
        }

        try {
            // In a real implementation, this would use JavaMail or a third-party email service
            // For now, we'll just log the email details
            log.info("MOCK EMAIL SENT:\nFrom: {}\nTo: {}\nSubject: {}\nContent: {}", 
                    fromEmail, to, subject, content);
            
            // Simulate a small delay as if actually sending an email
            Thread.sleep(100);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
}

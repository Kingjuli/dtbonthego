package com.dtbonthego.eventsservice.service.impl;

import com.dtbonthego.eventsservice.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SmsService interface.
 * This is a mock implementation that logs the SMS details instead of actually sending SMS messages.
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${notification.sms.enabled}")
    private boolean smsEnabled;

    @Value("${notification.sms.sender}")
    private String smsSender;

    /**
     * {@inheritDoc}
     * This is a mock implementation that logs the SMS details instead of actually sending SMS messages.
     */
    @Override
    public boolean sendSms(String to, String message) {
        if (!smsEnabled) {
            log.info("SMS notifications are disabled");
            return false;
        }

        try {
            // In a real implementation, this would use a third-party SMS service like Twilio or AWS SNS
            // For now, we'll just log the SMS details
            log.info("MOCK SMS SENT:\nFrom: {}\nTo: {}\nMessage: {}", 
                    smsSender, to, message);
            
            // Simulate a small delay as if actually sending an SMS
            Thread.sleep(100);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
}

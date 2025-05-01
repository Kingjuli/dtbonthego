package com.dtbonthego.eventsservice.service.impl;

import com.dtbonthego.common.exception.ResourceNotFoundException;
import com.dtbonthego.common.util.DateTimeUtils;
import com.dtbonthego.common.util.StringUtils;
import com.dtbonthego.eventsservice.model.Notification;
import com.dtbonthego.eventsservice.model.NotificationStatus;
import com.dtbonthego.eventsservice.model.NotificationType;
import com.dtbonthego.eventsservice.model.dto.NotificationDTO;
import com.dtbonthego.eventsservice.model.dto.TransactionEvent;
import com.dtbonthego.eventsservice.repository.NotificationRepository;
import com.dtbonthego.eventsservice.service.EmailService;
import com.dtbonthego.eventsservice.service.NotificationService;
import com.dtbonthego.eventsservice.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the NotificationService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<NotificationDTO> processTransactionEvent(TransactionEvent event) {
        log.info("Processing transaction event: {}", event);
        
        List<Notification> notifications = new ArrayList<>();
        
        // Create and send email notification if customer email is available
        if (event.getCustomerEmail() != null && !event.getCustomerEmail().isEmpty()) {
            Notification emailNotification = createEmailNotification(event);
            notifications.add(emailNotification);
        }
        
        // Create and send SMS notification if customer phone number is available
        if (event.getCustomerPhoneNumber() != null && !event.getCustomerPhoneNumber().isEmpty()) {
            Notification smsNotification = createSmsNotification(event);
            notifications.add(smsNotification);
        }
        
        // Save all notifications
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        
        // Send notifications asynchronously
        sendNotificationsAsync(savedNotifications);
        
        return savedNotifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByProfileId(Long profileId) {
        log.info("Getting notifications for profile ID: {}", profileId);
        return notificationRepository.findByProfileId(profileId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        log.info("Getting notification with ID: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.notificationNotFound(id));
        return mapToDTO(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByTransactionId(Long transactionId) {
        log.info("Getting notifications for transaction ID: {}", transactionId);
        return notificationRepository.findByTransactionId(transactionId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Creates an email notification for a transaction event.
     * 
     * @param event the transaction event
     * @return the created notification entity
     */
    private Notification createEmailNotification(TransactionEvent event) {
        String subject = generateEmailSubject(event);
        String content = generateEmailContent(event);
        
        return Notification.builder()
                .profileId(event.getProfileId())
                .type(NotificationType.EMAIL)
                .subject(subject)
                .content(content)
                .recipient(event.getCustomerEmail())
                .status(NotificationStatus.PENDING)
                .transactionId(event.getTransactionId())
                .build();
    }
    
    /**
     * Creates an SMS notification for a transaction event.
     * 
     * @param event the transaction event
     * @return the created notification entity
     */
    private Notification createSmsNotification(TransactionEvent event) {
        String content = generateSmsContent(event);
        
        return Notification.builder()
                .profileId(event.getProfileId())
                .type(NotificationType.SMS)
                .subject("Transaction Alert")
                .content(content)
                .recipient(event.getCustomerPhoneNumber())
                .status(NotificationStatus.PENDING)
                .transactionId(event.getTransactionId())
                .build();
    }
    
    /**
     * Generates the subject for an email notification based on the transaction event.
     * 
     * @param event the transaction event
     * @return the generated email subject
     */
    private String generateEmailSubject(TransactionEvent event) {
        return String.format("DTB On The Go: %s Transaction Alert", event.getTransactionType());
    }
    
    /**
     * Generates the content for an email notification based on the transaction event.
     * 
     * @param event the transaction event
     * @return the generated email content
     */
    private String generateEmailContent(TransactionEvent event) {
        StringBuilder content = new StringBuilder();
        String customerName = event.getCustomerName() != null ? event.getCustomerName() : "Valued Customer";
        
        content.append(String.format("Dear %s,\n\n", customerName));
        content.append("This is to inform you that a transaction has been processed on your account.\n\n");
        content.append("Transaction Details:\n");
        content.append(String.format("- Type: %s\n", event.getTransactionType()));
        content.append(String.format("- Amount: %s\n", formatAmount(event.getAmount())));
        content.append(String.format("- Status: %s\n", event.getStatus()));
        content.append(String.format("- Date/Time: %s\n", formatDateTime(event.getTimestamp())));
        
        if (event.getSourceAccountNumber() != null) {
            content.append(String.format("- From Account: %s\n", maskAccountNumber(event.getSourceAccountNumber())));
        }
        
        if (event.getDestinationAccountNumber() != null) {
            content.append(String.format("- To Account: %s\n", maskAccountNumber(event.getDestinationAccountNumber())));
        }
        
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            content.append(String.format("- Description: %s\n", event.getDescription()));
        }
        
        content.append("\nIf you did not authorize this transaction, please contact our customer support immediately.\n\n");
        content.append("Thank you for banking with DTB On The Go.\n\n");
        content.append("Regards,\nDTB On The Go Team");
        
        return content.toString();
    }
    
    /**
     * Generates the content for an SMS notification based on the transaction event.
     * 
     * @param event the transaction event
     * @return the generated SMS content
     */
    private String generateSmsContent(TransactionEvent event) {
        StringBuilder content = new StringBuilder();
        
        content.append(String.format("%s transaction of %s ", 
                event.getTransactionType(), formatAmount(event.getAmount())));
        
        if ("COMPLETED".equalsIgnoreCase(event.getStatus())) {
            content.append("successful");
        } else {
            content.append(String.format("(%s)", event.getStatus().toLowerCase()));
        }
        
        if (event.getSourceAccountNumber() != null) {
            content.append(String.format(" from Acc: %s", maskAccountNumber(event.getSourceAccountNumber())));
        }
        
        if (event.getDestinationAccountNumber() != null && !event.getTransactionType().equalsIgnoreCase("WITHDRAWAL")) {
            content.append(String.format(" to Acc: %s", maskAccountNumber(event.getDestinationAccountNumber())));
        }
        
        content.append(String.format(". %s", formatDateTime(event.getTimestamp())));
        
        return content.toString();
    }
    
    /**
     * Sends notifications asynchronously.
     * 
     * @param notifications the list of notifications to send
     */
    private void sendNotificationsAsync(List<Notification> notifications) {
        // In a real implementation, this would use an @Async method or a separate thread pool
        // For simplicity, we'll just use a new thread for now
        new Thread(() -> {
            for (Notification notification : notifications) {
                try {
                    boolean sent = false;
                    
                    if (notification.getType() == NotificationType.EMAIL) {
                        sent = emailService.sendEmail(
                                notification.getRecipient(),
                                notification.getSubject(),
                                notification.getContent());
                    } else if (notification.getType() == NotificationType.SMS) {
                        sent = smsService.sendSms(
                                notification.getRecipient(),
                                notification.getContent());
                    }
                    
                    // Update notification status
                    notification.setStatus(sent ? NotificationStatus.SENT : NotificationStatus.FAILED);
                    notification.setSentAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    
                } catch (Exception e) {
                    log.error("Failed to send notification: {}", e.getMessage(), e);
                    notification.setStatus(NotificationStatus.FAILED);
                    notificationRepository.save(notification);
                }
            }
        }).start();
    }
    
    /**
     * Maps a Notification entity to a NotificationDTO.
     * 
     * @param notification the notification entity
     * @return the notification DTO
     */
    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .profileId(notification.getProfileId())
                .type(notification.getType())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .recipient(notification.getRecipient())
                .status(notification.getStatus())
                .transactionId(notification.getTransactionId())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
    
    /**
     * Formats a BigDecimal amount for display.
     * 
     * @param amount the amount to format
     * @return the formatted amount
     */
    private String formatAmount(BigDecimal amount) {
        return String.format("$%.2f", amount);
    }
    
    /**
     * Formats a LocalDateTime for display.
     * 
     * @param dateTime the date/time to format
     * @return the formatted date/time
     */
    private String formatDateTime(LocalDateTime dateTime) {
        return DateTimeUtils.formatDateTime(dateTime);
    }
    
    /**
     * Masks an account number for security purposes.
     * 
     * @param accountNumber the account number to mask
     * @return the masked account number
     */
    private String maskAccountNumber(String accountNumber) {
        return StringUtils.maskAccountNumber(accountNumber);
    }
}

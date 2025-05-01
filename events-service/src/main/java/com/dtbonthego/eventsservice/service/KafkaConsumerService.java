package com.dtbonthego.eventsservice.service;

import com.dtbonthego.eventsservice.model.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for consuming Kafka messages from the Payment Service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    /**
     * Consumes transaction events from the Payment Service and processes them.
     *
     * @param event the transaction event
     */
    @KafkaListener(topics = "payment-transactions", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransactionEvent(TransactionEvent event) {
        log.info("Received transaction event: {}", event);
        try {
            notificationService.processTransactionEvent(event);
        } catch (Exception e) {
            log.error("Error processing transaction event: {}", e.getMessage(), e);
        }
    }
}

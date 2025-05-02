package com.dtbonthego.paymentservice.service.impl;

import com.dtbonthego.paymentservice.model.dto.TransactionEvent;
import com.dtbonthego.paymentservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the KafkaProducerService interface.
 * Handles publishing transaction events to Kafka topics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${spring.kafka.producer.transaction-topic:payment-transactions}")
    private String transactionsTopic;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishTransactionEvent(TransactionEvent event) {
        log.info("Publishing transaction event to Kafka: {}", event);
        
        try {
            // Use transaction ID as the key for partitioning
            String key = String.valueOf(event.getTransactionId());
            
            // Send the event to Kafka asynchronously
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.send(transactionsTopic, key, event);
            
            // Add callback to handle success/failure
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent event to topic {} with offset {}",
                            transactionsTopic, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send event to topic {}: {}", transactionsTopic, ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing transaction event to Kafka: {}", e.getMessage(), e);
            // Do not throw the exception to prevent transaction rollback
            // The notification is considered non-critical for transaction completion
        }
    }
} 
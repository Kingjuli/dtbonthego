package com.dtbonthego.paymentservice.service;

import com.dtbonthego.paymentservice.model.dto.TransactionEvent;

/**
 * Service interface for publishing transaction events to Kafka.
 */
public interface KafkaProducerService {
    
    /**
     * Publishes a transaction event to the Kafka topic.
     * 
     * @param event the transaction event to publish
     */
    void publishTransactionEvent(TransactionEvent event);
} 
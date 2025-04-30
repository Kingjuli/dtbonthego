package com.dtbonthego.profileservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple response object that contains a message.
 * Used for sending success/error messages back to the client.
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    /**
     * The message to send back to the client
     */
    private String message;
} 
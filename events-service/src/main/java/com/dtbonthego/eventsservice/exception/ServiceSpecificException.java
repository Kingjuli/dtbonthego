package com.dtbonthego.eventsservice.exception;

/**
 * Service-specific exception for the Events Service.
 * This is an example of how to create service-specific exceptions.
 */
public class ServiceSpecificException extends RuntimeException {

    /**
     * Constructs a new service-specific exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ServiceSpecificException(String message) {
        super(message);
    }

    /**
     * Constructs a new service-specific exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ServiceSpecificException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a notification delivery failure exception.
     *
     * @param notificationId the notification ID
     * @param reason the failure reason
     * @return the exception
     */
    public static ServiceSpecificException notificationDeliveryFailure(Long notificationId, String reason) {
        return new ServiceSpecificException(
                String.format("Failed to deliver notification %d: %s", notificationId, reason));
    }

    /**
     * Creates an invalid notification type exception.
     *
     * @param type the invalid notification type
     * @return the exception
     */
    public static ServiceSpecificException invalidNotificationType(String type) {
        return new ServiceSpecificException(
                String.format("Invalid notification type: %s", type));
    }
}

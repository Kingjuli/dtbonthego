package com.dtbonthego.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 * Provides common date/time formatting and parsing methods.
 */
public class DateTimeUtils {

    /**
     * Standard date-time formatter for the application (yyyy-MM-dd HH:mm:ss).
     */
    public static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Date-only formatter (yyyy-MM-dd).
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Time-only formatter (HH:mm:ss).
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Formats a LocalDateTime using the standard formatter.
     *
     * @param dateTime the date-time to format
     * @return the formatted date-time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(STANDARD_FORMATTER);
    }

    /**
     * Formats a LocalDateTime using the date-only formatter.
     *
     * @param dateTime the date-time to format
     * @return the formatted date string
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Formats a LocalDateTime using the time-only formatter.
     *
     * @param dateTime the date-time to format
     * @return the formatted time string
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(TIME_FORMATTER);
    }

    /**
     * Parses a date-time string using the standard formatter.
     *
     * @param dateTimeString the date-time string to parse
     * @return the parsed LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, STANDARD_FORMATTER);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private DateTimeUtils() {
        // Utility class, no instantiation
    }
}

package com.dtbonthego.common.util;

/**
 * Utility class for string operations.
 * Provides common string manipulation methods used across microservices.
 */
public class StringUtils {

    /**
     * Masks an account number for security purposes, showing only the last 4 digits.
     * For example, "1234567890" becomes "******7890".
     *
     * @param accountNumber the account number to mask
     * @return the masked account number
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        
        int visibleDigits = 4;
        int maskLength = accountNumber.length() - visibleDigits;
        StringBuilder masked = new StringBuilder();
        
        for (int i = 0; i < maskLength; i++) {
            masked.append("*");
        }
        
        masked.append(accountNumber.substring(maskLength));
        return masked.toString();
    }

    /**
     * Masks an email address for security purposes, showing only the first character
     * and the domain. For example, "john.doe@example.com" becomes "j***@example.com".
     *
     * @param email the email address to mask
     * @return the masked email address
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 1) {
            return email;
        }
        
        StringBuilder maskedUsername = new StringBuilder();
        maskedUsername.append(username.charAt(0));
        
        for (int i = 1; i < username.length(); i++) {
            maskedUsername.append("*");
        }
        
        return maskedUsername.toString() + "@" + domain;
    }

    /**
     * Masks a phone number for security purposes, showing only the last 4 digits.
     * For example, "+1234567890" becomes "******7890".
     *
     * @param phoneNumber the phone number to mask
     * @return the masked phone number
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() <= 4) {
            return phoneNumber;
        }
        
        int visibleDigits = 4;
        int maskLength = phoneNumber.length() - visibleDigits;
        StringBuilder masked = new StringBuilder();
        
        for (int i = 0; i < maskLength; i++) {
            masked.append("*");
        }
        
        masked.append(phoneNumber.substring(maskLength));
        return masked.toString();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private StringUtils() {
        // Utility class, no instantiation
    }
}

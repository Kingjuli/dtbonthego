package com.dtbonthego.storeofvalueservice.model;

/**
 * Enum representing different types of bank accounts.
 */
public enum AccountType {
    SAVINGS("Interest-bearing deposit account for long-term savings"),
    CURRENT("Transactional account for daily banking needs"),
    FIXED_DEPOSIT("Term deposit with higher interest rate for set period"),
    LOAN("Credit facility with scheduled repayment terms"),
    CREDIT("Revolving credit line for flexible borrowing");
    
    private final String description;
    
    AccountType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
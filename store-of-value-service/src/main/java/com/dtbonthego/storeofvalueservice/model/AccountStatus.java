package com.dtbonthego.storeofvalueservice.model;

/**
 * Enum representing the different states an account can be in.
 */
public enum AccountStatus {
    ACTIVE("Fully operational account with all services available"),
    INACTIVE("Temporarily dormant account with limited transactions"),
    SUSPENDED("Account with restricted access due to compliance issues"),
    CLOSED("Terminated account with no available services");
    
    private final String description;
    
    AccountStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
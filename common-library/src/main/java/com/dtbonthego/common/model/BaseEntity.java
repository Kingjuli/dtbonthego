package com.dtbonthego.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base entity class with common fields and behavior for all entities.
 * Provides audit timestamps (createdAt, updatedAt) and lifecycle callbacks.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Timestamp of when the entity was created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the entity was last updated
     */
    @Column
    private LocalDateTime updatedAt;

    /**
     * Sets creation timestamp upon entity creation
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Updates the update timestamp upon entity modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

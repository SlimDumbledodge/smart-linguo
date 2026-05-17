package com.smartlinguo.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "api_keys")
public class ApiKey extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(nullable = false)
    public UUID id;

    @Column(name = "keycloak_user_id", nullable = false)
    public String keycloakUserId;

    @Column(name = "key_hash", nullable = false)
    public String keyHash;

    @Column(name = "is_active")
    public boolean isActive = true;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

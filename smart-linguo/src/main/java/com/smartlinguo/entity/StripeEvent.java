package com.smartlinguo.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stripe_events")
public class StripeEvent extends PanacheEntityBase {

    @Id
    @UuidGenerator
    public UUID id;

    @Column(name = "stripe_event_id", nullable = false, unique = true)
    public String stripeEventId;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
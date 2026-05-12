package com.smartlinguo.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "translations",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "field_key",
            "target_lang"
        })
    }
)
public class Translation extends PanacheEntity {

    @Column(name = "source_lang", nullable = false, length = 10)
    public String sourceLang;

    @Column(name = "target_lang", nullable = false, length = 10)
    public String targetLang;

    @Column(name = "source_text", nullable = false, columnDefinition = "TEXT")
    public String sourceText;

    @Column(name = "translated_text", columnDefinition = "TEXT")
    public String translatedText;

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
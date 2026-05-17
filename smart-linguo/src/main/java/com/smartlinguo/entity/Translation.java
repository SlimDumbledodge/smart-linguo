package com.smartlinguo.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "translations")
public class Translation extends PanacheEntity {

    @Column(name = "translation_id", nullable = false)
    public UUID translationId;

    @Column(name = "api_key_id", nullable = false)
    public UUID apiKeyId;

    @Column(name = "index", nullable = false)
    public long index;

    @Column(name = "source_lang", nullable = false, length = 10)
    public String sourceLang;

    @Column(name = "target_lang", nullable = false, length = 10)
    public String targetLang;

    @Column(name = "source_text", nullable = false, columnDefinition = "TEXT")
    public String sourceText;

    @Column(name = "translated_text", columnDefinition = "TEXT")
    public String translatedText;

    @Column(name = "tokens_used", nullable = false)
    public long tokensUsed;

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
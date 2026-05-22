package com.smartlinguo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.smartlinguo.dto.response.TranslationResult;
import com.smartlinguo.entity.Translation;
import com.smartlinguo.enums.SupportedLanguage;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TranslationRepository implements PanacheRepository<Translation> {

    private static final int BATCH_SIZE = 50;
    private final EntityManager entityManager;

    public TranslationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void insertBatch(List<Translation> translations) {
        for (int i = 0; i < translations.size(); i++) {
            entityManager.persist(translations.get(i));

            if (i > 0 && i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    public Optional<TranslationResult> findTranslationById(UUID translationId) {
        List<Translation> rows = find("translationId = ?1 order by index asc", translationId).list();

        if (rows == null || rows.isEmpty()) {
            return Optional.empty();
        }

        SupportedLanguage source = SupportedLanguage.fromValue(rows.get(0).sourceLang);
        SupportedLanguage target = SupportedLanguage.fromValue(rows.get(0).targetLang);

        List<String> texts = rows.stream()
            .map(r -> r.translatedText)
            .toList();

        long totalTokens = rows.get(0).tokensUsed;

        return Optional.of(new TranslationResult(rows.get(0).translationId, source, target, texts, totalTokens));
    }
}

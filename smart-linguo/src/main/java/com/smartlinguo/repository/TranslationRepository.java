package com.smartlinguo.repository;

import java.util.List;

import com.smartlinguo.entity.Translation;

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
}

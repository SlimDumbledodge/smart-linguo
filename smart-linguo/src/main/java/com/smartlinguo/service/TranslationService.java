package com.smartlinguo.service;

import java.util.UUID;

import com.smartlinguo.dto.response.TranslationResult;
import com.smartlinguo.repository.TranslationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class TranslationService {

    private final TranslationRepository translationRepository;

    public TranslationService(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    public TranslationResult getById(UUID translationId) {
        return translationRepository.findTranslationById(translationId)
            .orElseThrow(() -> new NotFoundException("Translation not found for id: " + translationId));
    }
}

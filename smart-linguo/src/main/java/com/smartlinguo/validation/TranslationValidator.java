package com.smartlinguo.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlinguo.dto.request.CreateTranslationRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class TranslationValidator {

    private static final int MAX_SERIALIZED_REQUEST_LENGTH = 50_000;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void validate(CreateTranslationRequest request) {
        if (request.sourceLanguage() == null) {
            throw new BadRequestException("sourceLanguage is required");
        }
        if (request.targetLangs() == null || request.targetLangs().isEmpty()) {
            throw new BadRequestException("At least one target language is required");
        }
        if (request.texts() == null || request.texts().isEmpty()) {
            throw new BadRequestException("texts is required and must not be empty");
        }
        boolean hasInvalidText = request.texts().stream().anyMatch(text -> text == null || text.isBlank());
        if (hasInvalidText) {
            throw new BadRequestException("texts must contain only non-empty strings");
        }
        if (request.targetLangs().contains(request.sourceLanguage())) {
            throw new BadRequestException("sourceLanguage must not appear in targetLangs");
        }
        long distinctCount = request.targetLangs().stream().distinct().count();
        if (distinctCount != request.targetLangs().size()) {
            throw new BadRequestException("targetLangs must not contain duplicates");
        }

        validateSerializedRequestLength(request);
    }

    private void validateSerializedRequestLength(CreateTranslationRequest request) {
        try {
            String serializedRequest = MAPPER.writeValueAsString(request);
            if (serializedRequest.length() > MAX_SERIALIZED_REQUEST_LENGTH) {
                throw new BadRequestException("Serialized request must not exceed 50 000 characters");
            }
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid request payload", e);
        }
    }
}
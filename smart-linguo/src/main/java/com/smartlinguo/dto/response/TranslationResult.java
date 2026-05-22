package com.smartlinguo.dto.response;

import java.util.List;
import java.util.UUID;

import com.smartlinguo.enums.SupportedLanguage;

public record TranslationResult(
    UUID translationId,
    SupportedLanguage sourceLang,
    SupportedLanguage targetLang,
    List<String> texts,
    long totalTokens
) {}

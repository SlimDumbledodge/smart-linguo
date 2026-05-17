package com.smartlinguo.dto.response;

import java.util.List;

import com.smartlinguo.enums.SupportedLanguage;

public record TranslationResult(
    SupportedLanguage sourceLang,
    SupportedLanguage targetLang,
    List<String> texts,
    long totalTokens
) {}

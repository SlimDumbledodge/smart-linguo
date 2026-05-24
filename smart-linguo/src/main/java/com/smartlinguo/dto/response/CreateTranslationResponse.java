package com.smartlinguo.dto.response;

import java.util.List;

public record CreateTranslationResponse(
    List<TranslationResult> translations,
    long remainingTokens
) {}

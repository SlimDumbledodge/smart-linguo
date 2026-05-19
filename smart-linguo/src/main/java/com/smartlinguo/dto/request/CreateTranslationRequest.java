package com.smartlinguo.dto.request;

import java.util.List;
import com.smartlinguo.enums.SupportedLanguage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTranslationRequest(
    @NotNull(message = "sourceLanguage is required")
    SupportedLanguage sourceLanguage,

    @NotNull(message = "targetLangs is required")
    @NotEmpty(message = "targetLangs must not be empty")
    @Size(max = 5, message = "targetLangs must not exceed 5 languages")
    List<SupportedLanguage> targetLangs,

    @NotNull(message = "texts is required")
    @NotEmpty(message = "texts must not be empty")
    @Size(max = 100, message = "texts must not exceed 100 items")
    List<String> texts
) {}
package com.smartlinguo.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.UniqueElements;

import com.smartlinguo.enums.SupportedLanguage;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record CreateTranslationRequest(
    @NotNull(message = "sourceLanguage is required") 
    SupportedLanguage sourceLanguage,

    @NotNull(message = "targetLangs is required") 
    @UniqueElements
    List<SupportedLanguage> targetLangs,

    @NotNull
    @NotEmpty
    List<String> texts
) {}

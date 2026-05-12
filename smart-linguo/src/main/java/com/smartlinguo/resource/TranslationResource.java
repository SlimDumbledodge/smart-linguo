package com.smartlinguo.resource;

import com.smartlinguo.dto.CreateTranslationRequest;
import com.smartlinguo.service.OpenAiService;
import com.smartlinguo.validation.TranslationValidator;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/translate")
public class TranslationResource {

    private final OpenAiService openAiService;
    private final TranslationValidator translationValidator;

    public TranslationResource(OpenAiService openAiService, TranslationValidator translationValidator) {
        this.openAiService = openAiService;
        this.translationValidator = translationValidator;
    }

    @POST
    public void createTranslation(@Valid CreateTranslationRequest request) {
        this.translationValidator.validate(request);
        this.openAiService.createTranslation(request);
    }
}

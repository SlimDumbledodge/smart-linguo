package com.smartlinguo.resource;

import java.util.List;

import com.smartlinguo.dto.request.CreateTranslationRequest;
import com.smartlinguo.dto.response.TranslationResult;
import com.smartlinguo.service.OpenAiService;
import com.smartlinguo.validation.TranslationValidator;

import io.smallrye.mutiny.Uni;
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
    public Uni<List<TranslationResult>> createTranslation(@Valid CreateTranslationRequest request) {
        this.translationValidator.validate(request);
        return this.openAiService.createTranslation(request);
    }
}

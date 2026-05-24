package com.smartlinguo.resource;

import java.util.List;
import java.util.UUID;

import com.smartlinguo.dto.request.CreateTranslationRequest;
import com.smartlinguo.dto.response.CreateTranslationResponse;
import com.smartlinguo.dto.response.TranslationResult;
import com.smartlinguo.service.OpenAiService;
import com.smartlinguo.service.TranslationService;
import com.smartlinguo.validation.TranslationValidator;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;

@Path("/translate")
@RunOnVirtualThread
public class TranslationResource {

    @Context
    ContainerRequestContext requestContext;

    private final OpenAiService openAiService;
    private final TranslationValidator translationValidator;
    private final TranslationService translationService;

    public TranslationResource(OpenAiService openAiService, TranslationValidator translationValidator, TranslationService translationService) {
        this.openAiService = openAiService;
        this.translationValidator = translationValidator;
        this.translationService = translationService;
    }

    @POST
    public Uni<CreateTranslationResponse> createTranslation(@Valid CreateTranslationRequest request) {
        this.translationValidator.validate(request);

        UUID apiKeyId = (UUID) requestContext.getProperty("apiKeyId");
        String keycloakUserId = (String) requestContext.getProperty("keycloakUserId");

        if (apiKeyId == null || keycloakUserId == null) {
            throw new WebApplicationException(
                Response.status(401).entity("Authentification requise").build());
        }

        return this.openAiService.createTranslation(request, apiKeyId, keycloakUserId);
    }

    @GET
    @Path("/{translationId}")
    public TranslationResult findTranslationById(@PathParam("translationId") UUID translationId) {
        UUID apiKeyId = (UUID) requestContext.getProperty("apiKeyId");
        String keycloakUserId = (String) requestContext.getProperty("keycloakUserId");

        if (apiKeyId == null || keycloakUserId == null) {
            throw new jakarta.ws.rs.WebApplicationException(
                Response.status(401).entity("Authentification requise").build());
        }

        return this.translationService.getById(translationId);
    }
}

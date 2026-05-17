package com.smartlinguo.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.smartlinguo.service.ApiKeyService;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api-key")
@Authenticated
public class ApiKeyResource {

    private final ApiKeyService apiKeyService;

    @Inject
    JsonWebToken jwt;

    public ApiKeyResource(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @POST()
    @Path("/generate")
    public Response generateApiKey() {
        String keycloakUserId = jwt.getSubject(); 
        String rawKey = apiKeyService.generateApiKey(keycloakUserId);
        return Response.ok(rawKey).build();
    }
}

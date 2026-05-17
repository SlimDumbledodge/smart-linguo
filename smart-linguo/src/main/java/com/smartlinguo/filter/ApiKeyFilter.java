package com.smartlinguo.filter;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import com.smartlinguo.entity.ApiKey;
import com.smartlinguo.entity.UsageQuota;
import com.smartlinguo.repository.ApiKeyRepository;
import com.smartlinguo.repository.UsageQuotaRepository;
import com.smartlinguo.service.ApiKeyService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ApiKeyFilter {

    @Inject
    ApiKeyRepository apiKeyRepository;

    @Inject
    ApiKeyService apiKeyService;


    @Inject
    UsageQuotaRepository usageQuotaRepository;

    @ServerRequestFilter
    public Response filter(ContainerRequestContext ctx) {
        if (!ctx.getUriInfo().getPath().startsWith("/translate")) {
            return null;
        }

        String rawKey = ctx.getHeaderString("X-API-Key");
        if (rawKey == null || rawKey.isBlank()) {
            return Response.status(401).entity("Clé API manquante").build();
        }

        String keyHash = apiKeyService.hashRawKey(rawKey);
        ApiKey apiKey = apiKeyRepository.findByKeyHash(keyHash);

        if (apiKey == null) {
            return Response.status(401).entity("Clé API invalide").build();
        }

        UsageQuota quota = usageQuotaRepository.findByKeycloakUserId(apiKey.keycloakUserId);

        if (quota == null || quota.tokensRemaining <= 0) {
            return Response.status(429).entity("Plus de tokens disponibles").build();
        }

        ctx.setProperty("apiKeyId", apiKey.id);
        ctx.setProperty("keycloakUserId", apiKey.keycloakUserId);

        return null;
    }

}

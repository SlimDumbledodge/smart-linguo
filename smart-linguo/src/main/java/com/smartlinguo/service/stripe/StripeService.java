package com.smartlinguo.service.stripe;

import com.smartlinguo.entity.UsageQuota;
import com.smartlinguo.repository.UsageQuotaRepository;
import com.smartlinguo.service.keycloak.KeycloakAdminService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StripeService {

    @Inject
    KeycloakAdminService keycloakAdminService;

    @Inject
    UsageQuotaRepository usageQuotaRepository;

    @Transactional
    public void handleCheckoutCompleted(String email, long tokens) throws Exception {

        // 1. Récupérer ou créer le compte Keycloak
        String keycloakUserId = keycloakAdminService.getOrCreateUser(email);

        // 2. Chercher si un quota existe déjà
        UsageQuota quota = usageQuotaRepository.findByKeycloakUserId(keycloakUserId);

        if (quota == null) {
            // Première fois → créer le quota
            quota = new UsageQuota();
            quota.keycloakUserId = keycloakUserId;
            quota.tokensRemaining = tokens;
            usageQuotaRepository.persist(quota);
        } else {
            // Rechargement → incrémenter
            quota.tokensRemaining += tokens;
        }
    }
}
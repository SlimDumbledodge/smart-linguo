package com.smartlinguo.service.stripe;

import com.smartlinguo.config.stripe.PriceCatalogConfig;
import com.smartlinguo.entity.StripeEvent;
import com.smartlinguo.entity.UsageQuota;
import com.smartlinguo.repository.UsageQuotaRepository;
import com.smartlinguo.repository.stripe.StripeEventRepository;
import com.smartlinguo.service.keycloak.KeycloakAdminService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StripeService {

    private static final Logger LOG = Logger.getLogger(StripeService.class);

    @Inject
    KeycloakAdminService keycloakAdminService;

    @Inject
    UsageQuotaRepository usageQuotaRepository;

    @Inject
    PriceCatalogConfig priceCatalogConfig;

    @Inject
    StripeEventRepository stripeEventRepository;

    @Transactional
    public void handleCheckoutCompleted(String eventId, String email, String priceId) throws Exception {

        if (stripeEventRepository.existsByStripeEventId(eventId)) {
            LOG.infof("Event Stripe %s déjà traité, ignoré", eventId);
            return;
        }

        StripeEvent stripeEvent = new StripeEvent();
        stripeEvent.stripeEventId = eventId;
        stripeEventRepository.persist(stripeEvent);

        long tokens = priceCatalogConfig.getTokensForPrice(priceId);

        String keycloakUserId = keycloakAdminService.getOrCreateUser(email);
        LOG.infof("Utilisateur Keycloak résolu : %s pour %s", keycloakUserId, email);

        UsageQuota quota = usageQuotaRepository.findByKeycloakUserId(keycloakUserId);
        if (quota == null) {
            quota = new UsageQuota();
            quota.keycloakUserId = keycloakUserId;
            quota.tokensRemaining = tokens;
            usageQuotaRepository.persist(quota);
            LOG.infof("Quota créé pour %s - %d tokens", email, tokens);
        } else {
            quota.tokensRemaining += tokens;
            LOG.infof("Quota rechargé pour %s - +%d tokens, total : %d", email, tokens, quota.tokensRemaining);
        }
    }
}
package com.smartlinguo.service.stripe;

import com.smartlinguo.config.stripe.PriceCatalogConfig;
import com.smartlinguo.entity.UsageQuota;
import com.smartlinguo.repository.UsageQuotaRepository;
import com.smartlinguo.repository.stripe.StripeEventRepository;
import com.smartlinguo.service.keycloak.KeycloakAdminService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class StripeServiceTest {

    @Inject
    StripeService stripeService;

    @Inject
    StripeEventRepository stripeEventRepository;

    @Inject
    UsageQuotaRepository usageQuotaRepository;

    @InjectMock
    KeycloakAdminService keycloakAdminService;

    @BeforeEach
    @Transactional
    void cleanDb() {
        stripeEventRepository.deleteAll();
        usageQuotaRepository.deleteAll();
    }

    @Test

    void shouldIgnoreEventAlreadyHandle() throws Exception {
        // On retourne un faux keycloakId à partir d'un email
        Mockito.when(keycloakAdminService.getOrCreateUser("user@test.com"))
               .thenReturn("fake-keycloak-id");

        String eventId = "evt_test_idempotence";
        String email = "user@test.com";
        String priceId = "price_1TXsVfLZECt0i14XNAfvc4wA"; // Starter Plan => 50k tokens

        // 2 appels avec le même eventId
        stripeService.handleCheckoutCompleted(eventId, email, priceId);
        stripeService.handleCheckoutCompleted(eventId, email, priceId);

        // 2ème appel devrait être ignoré
        Mockito.verify(keycloakAdminService, Mockito.times(1))
               .getOrCreateUser(email);

        // Quota a été créé UNE SEULE fois et pas 2 (50 000, pas 100 000)
        UsageQuota quota = usageQuotaRepository.findByKeycloakUserId("fake-keycloak-id");
        assertEquals(50_000L, quota.tokensRemaining);
    }

    @Test
    void shouldRejectUnknownPriceId() throws Exception {
        // On retourne un faux keycloakId à partir d'un email
        Mockito.when(keycloakAdminService.getOrCreateUser("user@test.com"))
               .thenReturn("fake-keycloak-id");

        // priceId inconnu => THROWS EXCEPTION
        assertThrows(IllegalArgumentException.class, () ->
            stripeService.handleCheckoutCompleted("evt_test_fraud", "user@test.com", "price_FAKE")
        );

        // Vérifie si aucun quota n'a été créé
        UsageQuota quota = usageQuotaRepository.findByKeycloakUserId("fake-keycloak-id");
        assertNull(quota);
    }
}
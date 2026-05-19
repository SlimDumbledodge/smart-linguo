package com.smartlinguo.config.stripe;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class PriceCatalogConfig {

    private static final Map<String, Long> PRICE_TO_TOKENS = Map.of(
        "price_1TXsVfLZECt0i14XNAfvc4wA", 50_000L,
        "price_1TXsVvLZECt0i14XaTdJ7HTN", 500_000L,
        "price_1TXsWELZECt0i14Xji1hk7hM", 5_000_000L
    );

    public long getTokensForPrice(String priceId) {
        Long tokens = PRICE_TO_TOKENS.get(priceId);
        if (tokens == null) {
            throw new IllegalArgumentException("priceId inconnu : " + priceId);
        }
        return tokens;
    }
}
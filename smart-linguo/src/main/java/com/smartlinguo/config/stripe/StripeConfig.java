package com.smartlinguo.config.stripe;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class StripeConfig {

    @ConfigProperty(name = "stripe.secret-key")
    String secretKey;

    @ConfigProperty(name = "stripe.webhook-secret")
    String webhookSecret;

    public String getSecretKey() { return secretKey; }
    public String getWebhookSecret() { return webhookSecret; }
}
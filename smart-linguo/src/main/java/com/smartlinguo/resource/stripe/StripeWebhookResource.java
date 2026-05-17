package com.smartlinguo.resource.stripe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlinguo.config.stripe.StripeConfig;
import com.smartlinguo.service.stripe.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/webhook")
@ApplicationScoped
public class StripeWebhookResource {

    @Inject
    StripeConfig stripeConfig;

    @Inject
    StripeService stripeService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @POST
    @Path("/stripe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response handle(String payload, @HeaderParam("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            return Response.status(400).entity("Signature invalide").build();
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonNode session = mapper.readTree(rawJson);

                String email = session.path("customer_details").path("email").asText();
                String tokensRaw = session.path("metadata").path("tokens").asText();

                if (tokensRaw.isBlank()) {
                    System.out.println("metadata tokens manquant, event ignoré");
                    return Response.ok().build();
                }

                long tokens = Long.parseLong(tokensRaw);
                stripeService.handleCheckoutCompleted(email, tokens);

            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(500).entity("Erreur : " + e.getMessage()).build();
            }
        }

        return Response.ok().build();
    }
}
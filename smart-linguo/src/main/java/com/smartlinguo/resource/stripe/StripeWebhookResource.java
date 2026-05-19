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
import org.jboss.logging.Logger;

@Path("/webhook")
@ApplicationScoped
public class StripeWebhookResource {

    private static final Logger LOG = Logger.getLogger(StripeWebhookResource.class);

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
            LOG.warn("Webhook Stripe rejeté - signature invalide");
            return Response.status(400).entity("Signature invalide").build();
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonNode session = mapper.readTree(rawJson);
                String email = session.path("customer_details").path("email").asText();
                String priceId = session.path("metadata").path("priceId").asText();

                if (priceId.isBlank()) {
                    LOG.warnf("Webhook %s ignoré - metadata priceId manquant", event.getId());
                    return Response.ok().build();
                }

                LOG.infof("Webhook %s reçu - checkout.session.completed pour %s", event.getId(), email);
                stripeService.handleCheckoutCompleted(event.getId(), email, priceId);

            } catch (Exception e) {
                LOG.errorf(e, "Erreur inattendue lors du traitement du webhook %s", event.getId());
                return Response.status(500).entity("Erreur : " + e.getMessage()).build();
            }
        }

        return Response.ok().build();
    }
}
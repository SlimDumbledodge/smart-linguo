package com.smartlinguo.resource.stripe;

import com.smartlinguo.config.stripe.StripeConfig;
import com.smartlinguo.dto.request.stripe.CheckoutRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/checkout")
@ApplicationScoped
public class StripeCheckoutSessionResource {

    @Inject
    StripeConfig stripeConfig;

    @POST
    @Path("/create-session")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSession(CheckoutRequest request) {

        Stripe.apiKey = stripeConfig.getSecretKey();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://ton-dashboard.com/success")
                    .setCancelUrl("https://ton-site-statique.com/pricing")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(request.getPriceId())
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("email", request.getEmail())
                    .putMetadata("tokens", String.valueOf(request.getTokens()))
                    .build();

            Session session = Session.create(params);

            return Response.ok(Map.of("url", session.getUrl())).build();

        } catch (StripeException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
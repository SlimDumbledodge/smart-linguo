package com.smartlinguo.repository.stripe;

import com.smartlinguo.entity.StripeEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StripeEventRepository implements PanacheRepository<StripeEvent> {

    public boolean existsByStripeEventId(String stripeEventId) {
        return find("stripeEventId", stripeEventId).firstResult() != null;
    }
}
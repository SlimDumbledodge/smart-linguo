package com.smartlinguo.repository;

import java.util.UUID;

import com.smartlinguo.entity.UsageQuota;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsageQuotaRepository implements PanacheRepositoryBase<UsageQuota, UUID> {
    public UsageQuota findByKeycloakUserId(String keycloakUserId) {
        return find("keycloakUserId", keycloakUserId).firstResult();
    }
}

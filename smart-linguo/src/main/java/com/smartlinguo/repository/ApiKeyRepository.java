package com.smartlinguo.repository;

import com.smartlinguo.entity.ApiKey;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApiKeyRepository implements PanacheRepository<ApiKey> {

    public ApiKey findByKeyHash(String hash) {
        return find("keyHash = ?1 and isActive = true", hash).firstResult();
    }
}
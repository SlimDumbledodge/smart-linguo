package com.smartlinguo.service;

import com.smartlinguo.entity.ApiKey;
import com.smartlinguo.repository.ApiKeyRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Transactional
    public String generateApiKey(String keycloakUserId) {
        String rawKey = generateRawKey();
        String keyHash = hashRawKey(rawKey);

        ApiKey apiKey = new ApiKey();
        apiKey.keyHash = keyHash;
        apiKey.keycloakUserId = keycloakUserId;
        apiKeyRepository.persist(apiKey);

        return rawKey;
    }

    public String hashRawKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 non disponible", e);
        }
    }

    private String generateRawKey() {
        return "sk-" + UUID.randomUUID().toString().replace("-", "");
    }
}
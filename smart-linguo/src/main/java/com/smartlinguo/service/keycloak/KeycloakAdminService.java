package com.smartlinguo.service.keycloak;

import com.smartlinguo.config.keycloak.KeycloakAdminConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Form;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class KeycloakAdminService {

    @Inject
    KeycloakAdminConfig config;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // 1. Obtenir un token admin
    private String getAdminToken() throws Exception {
        String tokenUrl = config.getUrl() + "/realms/" + config.getMasterRealm() + "/protocol/openid-connect/token";

        String form = "grant_type=password"
                + "&client_id=" + config.getClientId()
                + "&username=" + config.getUsername()
                + "&password=" + config.getPassword();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Impossible d'obtenir le token admin Keycloak : " + response.body());
        }

        JsonNode json = mapper.readTree(response.body());
        return json.get("access_token").asText();
    }

    // 2. Chercher un user par email → retourne son keycloakUserId ou null
    public String findUserByEmail(String email) throws Exception {
        String adminToken = getAdminToken();
        String usersUrl = config.getUrl() + "/admin/realms/" + config.getUserRealm() + "/users?email=" + email + "&exact=true";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(usersUrl))
                .header("Authorization", "Bearer " + adminToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur recherche user Keycloak : " + response.body());
        }

        JsonNode users = mapper.readTree(response.body());

        if (users.isEmpty()) {
            return null; // user n'existe pas
        }

        return users.get(0).get("id").asText(); // retourne le keycloakUserId
    }

    public String createUser(String email) throws Exception {
        String adminToken = getAdminToken();
        String usersUrl = config.getUrl() + "/admin/realms/" + config.getUserRealm() + "/users";

        // Construction du body proprement
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("temporary", true);

        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", email);
        userBody.put("username", email);
        userBody.put("enabled", true);
        userBody.put("emailVerified", true);

        String body = mapper.writeValueAsString(userBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(usersUrl))
                .header("Authorization", "Bearer " + adminToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            throw new RuntimeException("Erreur création user Keycloak : " + response.body());
        }

        String location = response.headers().firstValue("Location").orElseThrow();
        return location.substring(location.lastIndexOf("/") + 1);
    }

    public void sendResetPasswordEmail(String userId) throws Exception {
        String adminToken = getAdminToken();
        String url = config.getUrl() + "/admin/realms/" + config.getUserRealm()
                + "/users/" + userId + "/execute-actions-email";

        String body = mapper.writeValueAsString(List.of("UPDATE_PASSWORD"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + adminToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Erreur envoi reset password email : " + response.body());
        }
    }

    // 4. Récupérer ou créer un user Keycloak → retourne toujours un keycloakUserId
    public String getOrCreateUser(String email) throws Exception {
        String keycloakUserId = findUserByEmail(email);

        if (keycloakUserId != null) {
            return keycloakUserId;
        }

        String newUserId = createUser(email);
        sendResetPasswordEmail(newUserId);
        return newUserId;
    }
}
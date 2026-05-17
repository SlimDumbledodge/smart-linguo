package com.smartlinguo.config.keycloak;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KeycloakAdminConfig {

    @ConfigProperty(name = "keycloak.admin.url")
    String url;

    @ConfigProperty(name = "keycloak.admin.master-realm")
    String masterRealm;

    @ConfigProperty(name = "keycloak.admin.user-realm")
    String userRealm;

    @ConfigProperty(name = "keycloak.admin.client-id")
    String clientId;

    @ConfigProperty(name = "keycloak.admin.username")
    String username;

    @ConfigProperty(name = "keycloak.admin.password")
    String password;

    public String getUrl() { return url; }
    public String getMasterRealm() { return masterRealm; }
    public String getUserRealm() { return userRealm; }
    public String getClientId() { return clientId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
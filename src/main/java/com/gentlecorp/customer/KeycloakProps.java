package com.gentlecorp.customer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Konfigurationseigenschaften für die Keycloak-Integration.
 * <p>
 * Diese Klasse ermöglicht das Laden von Keycloak-spezifischen Einstellungen aus `application.yml`.
 * </p>
 *
 * @param schema       Das Schema für die Keycloak-Verbindung (z. B. `https` oder `http`).
 * @param host         Der Hostname oder die IP-Adresse des Keycloak-Servers.
 * @param port         Der Port, unter dem Keycloak erreichbar ist.
 * @param clientId     Die Client-ID für die Authentifizierung mit Keycloak.
 * @param clientSecret Das Client-Secret für die Authentifizierung mit Keycloak.
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProps(
  String schema,
  String host,
  int port,
  String clientId,
  String clientSecret
) {
}

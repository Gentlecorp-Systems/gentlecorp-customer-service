package com.gentlecorp.customer.config;

import com.gentlecorp.customer.security.KeycloakRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Diese Schnittstelle definiert die Konfiguration für den Zugriff auf einen Keycloak-Server.
 * Sie stellt eine Methode zur Verfügung, um eine REST-Client-Schnittstelle für die Keycloak-Integration zu erzeugen.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
sealed interface KeycloakClientConfig permits ApplicationConfig {

  /**
   * Logger für die Protokollierung von Keycloak-Client-Ereignissen.
   */
  Logger LOGGER = LoggerFactory.getLogger(KeycloakClientConfig.class);

  /**
   * Erstellt und konfiguriert eine Instanz des Keycloak-Repository-Clients.
   *
   * @param clientBuilder Builder-Objekt für den REST-Client.
   * @return Eine konfigurierte Instanz des `KeycloakRepository`.
   */
  @Bean
  default KeycloakRepository keycloakRepository(final RestClient.Builder clientBuilder) {
    final var kcDefaultPort = 18080;
    final var kcSchemaEnv = EnvConfig.get("KC_SERVICE_SCHEMA");
    final var kcHostEnv = EnvConfig.get("KC_SERVICE_HOST");
    final var kcPortEnv = EnvConfig.get("KC_SERVICE_PORT");

    final var schema = kcSchemaEnv == null ? "http" : kcSchemaEnv;
    final var host = kcHostEnv == null ? "localhost" : kcHostEnv;
    int port;
    try {
      port = kcPortEnv == null ? kcDefaultPort : Integer.parseInt(kcPortEnv);
    } catch (NumberFormatException e) {
      LOGGER.warn("Ungültiger Portwert in KC_SERVICE_PORT: '{}'. Fallback auf {}", kcPortEnv, kcDefaultPort);
      port = kcDefaultPort;
    }

    final var baseUri = UriComponentsBuilder.newInstance()
        .scheme(schema)
        .host(host)
        .port(port)
        .build();

    LOGGER.debug("KeycloakRepository: baseUri={}", baseUri);

    final var restClient = clientBuilder.baseUrl(baseUri.toUriString()).build();
    final var clientAdapter = RestClientAdapter.create(restClient);
    final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();

    return proxyFactory.createClient(KeycloakRepository.class);
  }
}

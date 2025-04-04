package com.gentlecorp.customer.config;

import com.gentlecorp.customer.security.dto.TokenDTO;
import com.gentlecorp.customer.testData.CustomerTestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TestClientProvider extends CustomerTestData {

  private static final Logger log = LoggerFactory.getLogger(TestClientProvider.class);
  private HttpGraphQlClient graphQlClient;
  private int serverPort;

  private final Map<String, String> tokenCache = new HashMap<>();

  public TestClientProvider() {
  }

  /**
   * Initialisiert die Authentifizierungsclients für verschiedene Benutzerrollen.
   */
  public void init(final int port) {
    log.info("Initialisiere Testclients mit Port {}", port);
    serverPort = port;

    WebClient webClient = WebClient.builder()
        .baseUrl(SCHEMA_HOST + serverPort + GRAPHQL_ENDPOINT)
        .defaultHeader("Content-Type", "application/json")
        .build();

    this.graphQlClient = HttpGraphQlClient.builder(webClient).build();

    tokenCache.put(USER_ADMIN, authenticate(USER_ADMIN, USER_PASSWORD));
    tokenCache.put(USER_USER, authenticate(USER_USER, USER_PASSWORD));
    tokenCache.put(USER_BASIC, authenticate(USER_BASIC, USER_PASSWORD));
    tokenCache.put(USER_ELITE, authenticate(USER_ELITE, USER_PASSWORD));
    tokenCache.put(USER_SUPREME, authenticate(USER_SUPREME, USER_PASSWORD));
  }

  /**
   * Führt die Authentifizierung durch und gibt das Token zurück.
   *
   * @param username Benutzername
   * @param password Passwort
   * @return JWT-Token als String
   */
  public String authenticate(String username, String password) {
    final var query = """
        mutation Login($username: String!, $password: String!) {
            authenticate(username: $username, password: $password) {
                access_token
            }
        }
    """;

    //log.debug("query: {}", query);
    log.info("Authenticate user {}", username);

    Map<String, Object> variables = Map.of(
        "username",  username,
        "password", password
    );

    final var token = graphQlClient
        .mutate()
        .build()
        .document(query)
        .variables(variables)
        .retrieveSync("authenticate")
        .toEntity(TokenDTO.class);

    return token.access_token();
  }

  /**
   * Erstellt einen `WebClient` mit Authentifizierung für eine bestimmte Rolle.
   *
   * @param role Benutzerrolle
   * @return Authentifizierter `WebClient`
   */
  public HttpGraphQlClient getAuthenticatedClient(String role) {
    String token = tokenCache.get(role);
    if (token == null) {
      throw new RuntimeException("Kein Token für Rolle: " + role);
    }


    WebClient webClient = WebClient.builder()
        .baseUrl(SCHEMA_HOST + serverPort + GRAPHQL_ENDPOINT)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .build();

    return  HttpGraphQlClient.builder(webClient).build();
  }

  public HttpGraphQlClient getVisitorClient() {
    WebClient webClient = WebClient.builder()
        .baseUrl(SCHEMA_HOST + serverPort + GRAPHQL_ENDPOINT)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    return HttpGraphQlClient.builder(webClient).build();
  }

  public HttpGraphQlClient createAuthenticatedClient(String username, String password) {
    final var token = authenticate(username,password);

    WebClient webClient = WebClient.builder()
        .baseUrl(SCHEMA_HOST + serverPort + GRAPHQL_ENDPOINT)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .build();

    return  HttpGraphQlClient.builder(webClient).build();
  }
}

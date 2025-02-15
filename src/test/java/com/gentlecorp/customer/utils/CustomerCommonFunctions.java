package com.gentlecorp.customer.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
import com.gentlecorp.customer.model.CustomGraphQLError;
import com.gentlecorp.customer.model.GraphQlResponse;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.testData.CustomerTestData;

import graphql.language.SourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(Env.class)
public class CustomerCommonFunctions extends CustomerTestData {

  private static final Logger log = LoggerFactory.getLogger(CustomerCommonFunctions.class);
  @Autowired
  protected TestClientProvider testClientProvider;

  public final Env env = new Env();

  @Value("${server.port}")
  public int serverPort;

  @BeforeAll
  protected void setup() {
    new Env();
  }

  /**
   * Führt eine GraphQL-Anfrage aus und gibt die Antwort zurück.
   *
   * @param query Die GraphQL-Query als String
   * @param client Der `WebClient` für die Authentifizierung
   * @return Die Antwort als JSON-String
   */
  protected String executeGraphQLQuery(String query, WebClient client) {
    return client.post()
        .uri(GRAPHQL_ENDPOINT)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(Map.of("query", query))
        .retrieve()
        .bodyToMono(String.class)
        .block(); // block() nur für Tests
  }

  protected GraphQlResponse<List<Customer>> executeCustomersGraphQLQuery(final String query, final Map<String, Object> variables, final HttpGraphQlClient client) {
    return client.document(query)
        .variables(Optional.ofNullable(variables).orElseGet(HashMap::new))
        .retrieve("customers")
        .toEntityList(Customer.class)
        .map(customer -> new GraphQlResponse<>(customer, List.of())) // Erfolgreiche Antwort
        .onErrorResume(error -> {
          log.error("Fehler aufgetreten: {}", error.getClass().getName());
          log.error("Fehlermeldung (Original): {}", error.getMessage());

          if (error instanceof FieldAccessException fieldAccessException) {
            String errorMessage = fieldAccessException.getMessage();

            // Werte initialisieren
            String extractedMessage = "Unbekannter Fehler";
            List<String> path = List.of();
            List<SourceLocation> locations = List.of();
            Map<String, Object> extensions = Map.of();

            try {
              // 🔹 1. `message=` Wert extrahieren
              Matcher messageMatcher = Pattern.compile("message=(.*?), locations=").matcher(errorMessage);
              if (messageMatcher.find()) {
                extractedMessage = messageMatcher.group(1).trim();
                log.debug("Extracted message: {}", extractedMessage);
              }

              // 🔹 2. `path=` Wert extrahieren
              Matcher pathMatcher = Pattern.compile("path=\\[([^]]+)\\]").matcher(errorMessage);
              if (pathMatcher.find()) {
                path = Arrays.asList(pathMatcher.group(1).replace(" ", "").split(","));
                log.debug("Extracted path: {}", path);
              }

              // 🔹 3. `locations=` Wert extrahieren
              Matcher locationsMatcher = Pattern.compile("locations=\\[\\{line=(\\d+), column=(\\d+)\\}\\]").matcher(errorMessage);
              if (locationsMatcher.find()) {
                int line = Integer.parseInt(locationsMatcher.group(1));
                int column = Integer.parseInt(locationsMatcher.group(2));
                locations = List.of(new SourceLocation(line, column));
                log.debug("Extracted locations: {}", locations);
              }

              // 🔹 4. `extensions=` manuell in JSON umwandeln
              Matcher extensionsMatcher = Pattern.compile("extensions=\\{([^}]+)\\}").matcher(errorMessage);
              if (extensionsMatcher.find()) {
                String extensionsString = extensionsMatcher.group(1).trim();

                // 🛠 `=` durch `:` ersetzen und String-Werte mit `"` umschließen
                String jsonFormattedExtensions = extensionsString
                    .replaceAll("(\\w+)=([A-Za-z_]+)", "\"$1\":\"$2\"") // Keys und Werte korrekt setzen
                    .replaceAll("=", ":") // Falls noch = übrig ist, zu : umwandeln
                    .replaceAll("'", "\""); // Falls Einzel-Anführungszeichen vorkommen

                jsonFormattedExtensions = "{" + jsonFormattedExtensions + "}"; // `{}` wieder hinzufügen

                log.debug("Endgültige JSON-Extensions: {}", jsonFormattedExtensions);

                ObjectMapper objectMapper = new ObjectMapper();
                extensions = objectMapper.readValue(jsonFormattedExtensions, new TypeReference<Map<String, Object>>() {});
              }


              log.error("Extrahierte GraphQL-Fehlermeldung: {}", extractedMessage);
              log.error("Extrahrierter Path: {}", path);
              log.error("Extrahrierte Extensions: {}", extensions);

              return Mono.just(new GraphQlResponse<>(null, List.of(
                  new CustomGraphQLError(extractedMessage, locations, null, extensions)
              )));

            } catch (Exception ex) {
              log.error("Fehler beim Parsen des GraphQL-Fehlers: {}", ex.getMessage());
            }
          }

          // Fallback für nicht erkannte Fehler
          return Mono.just(new GraphQlResponse<>(null, List.of(new CustomGraphQLError(error.getMessage()))));
        })
        .block();
  }

  protected GraphQlResponse<Customer> executeCustomerGraphQLQuery(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return client.document(query)
        .variables(variables)
        .retrieve("customer") // Hier wird sichergestellt, dass nur das gewünschte Feld extrahiert wird
        //.toEntity(new ParameterizedTypeReference<GraphQlResponse<Customer>>() {}) // Richtige Typisierung
        .toEntity(Customer.class) // In ein Customer-Objekt umwandeln
        .map(customer -> new GraphQlResponse<>(customer, List.of())) // Erfolgreiche Antwort
        .onErrorResume(error -> {
          log.error("Fehler aufgetreten: {}", error.getClass().getName());
          log.error("Fehlermeldung (Original): {}", error.getMessage());

          if (error instanceof FieldAccessException fieldAccessException) {
            String errorMessage = fieldAccessException.getMessage();

            // Werte initialisieren
            String extractedMessage = "Unbekannter Fehler";
            List<String> path = List.of();
            List<SourceLocation> locations = List.of();
            Map<String, Object> extensions = Map.of();

            try {
              // 🔹 1. `message=` Wert extrahieren
              Matcher messageMatcher = Pattern.compile("message=(.*?), locations=").matcher(errorMessage);
              if (messageMatcher.find()) {
                extractedMessage = messageMatcher.group(1).trim();
                log.debug("Extracted message: {}", extractedMessage);
              }

              // 🔹 2. `path=` Wert extrahieren
              Matcher pathMatcher = Pattern.compile("path=\\[([^]]+)\\]").matcher(errorMessage);
              if (pathMatcher.find()) {
                path = Arrays.asList(pathMatcher.group(1).replace(" ", "").split(","));
                log.debug("Extracted path: {}", path);
              }

              // 🔹 3. `locations=` Wert extrahieren
              Matcher locationsMatcher = Pattern.compile("locations=\\[\\{line=(\\d+), column=(\\d+)\\}\\]").matcher(errorMessage);
              if (locationsMatcher.find()) {
                int line = Integer.parseInt(locationsMatcher.group(1));
                int column = Integer.parseInt(locationsMatcher.group(2));
                locations = List.of(new SourceLocation(line, column));
                log.debug("Extracted locations: {}", locations);
              }

              // 🔹 4. `extensions=` manuell in JSON umwandeln
              Matcher extensionsMatcher = Pattern.compile("extensions=\\{([^}]+)\\}").matcher(errorMessage);
              if (extensionsMatcher.find()) {
                String extensionsString = extensionsMatcher.group(1).trim();

                // 🛠 `=` durch `:` ersetzen und String-Werte mit `"` umschließen
                String jsonFormattedExtensions = extensionsString
                    .replaceAll("(\\w+)=([A-Za-z_]+)", "\"$1\":\"$2\"") // Keys und Werte korrekt setzen
                    .replaceAll("=", ":") // Falls noch = übrig ist, zu : umwandeln
                    .replaceAll("'", "\""); // Falls Einzel-Anführungszeichen vorkommen

                jsonFormattedExtensions = "{" + jsonFormattedExtensions + "}"; // `{}` wieder hinzufügen

                log.debug("Endgültige JSON-Extensions: {}", jsonFormattedExtensions);

                ObjectMapper objectMapper = new ObjectMapper();
                extensions = objectMapper.readValue(jsonFormattedExtensions, new TypeReference<Map<String, Object>>() {});
              }


              log.error("Extrahierte GraphQL-Fehlermeldung: {}", extractedMessage);
              log.error("Extrahrierter Path: {}", path);
              log.error("Extrahrierte Extensions: {}", extensions);

              return Mono.just(new GraphQlResponse<>(null, List.of(
                  new CustomGraphQLError(extractedMessage, locations, null, extensions)
              )));

            } catch (Exception ex) {
              log.error("Fehler beim Parsen des GraphQL-Fehlers: {}", ex.getMessage());
            }
          }

          // Fallback für nicht erkannte Fehler
          return Mono.just(new GraphQlResponse<>(null, List.of(new CustomGraphQLError(error.getMessage()))));
        })
        .block();
  }






  /**
   * Erstellt eine neue Kundenanfrage.
   *
   * @param username Benutzername des Kunden
   * @param email E-Mail-Adresse des Kunden
   * @param tierLevel Stufe des Kunden
   * @return ID des erstellten Kunden
   */
//  protected String createCustomer(String username, String email, int tierLevel) {
//    WebClient visitorClient = testClientProvider.getVisitorClient();
//
//    String mutation = """
//            mutation {
//                createCustomer(input: {
//                    username: "%s",
//                    email: "%s",
//                    tierLevel: %d
//                }) {
//                    id
//                }
//            }
//        """.formatted(username, email, tierLevel);
//
//    String response = executeGraphQLQuery(mutation, visitorClient);
//    assertThat(response).contains("id");
//
//    return extractIdFromResponse(response);
//  }

  /**
   * Holt Kundeninformationen als Admin.
   *
   * @param customerId ID des Kunden
   * @return `TestCustomer`-Objekt
   */
//  protected TestCustomer getCustomerAsAdmin(String customerId) {
//    WebClient adminClient = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
//
//    String query = """
//            { customer(id: "%s") { username email tierLevel } }
//        """.formatted(customerId);
//
//    String response = executeGraphQLQuery(query, adminClient);
//    assertThat(response).contains("username", "email", "tierLevel");
//
//    return parseCustomerFromResponse(response);
//  }

//  /**
//   * Löscht einen Kunden als Admin.
//   *
//   * @param customerId ID des Kunden
//   */
//  protected void deleteCustomer(String customerId) {
//    WebClient adminClient = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
//
//    String mutation = """
//            mutation {
//                deleteCustomer(id: "%s")
//            }
//        """.formatted(customerId);
//
//    String response = executeGraphQLQuery(mutation, adminClient);
//    assertThat(response).contains("true");
//
//    // Verifizieren, dass der Kunde gelöscht wurde
//    String query = """
//            { customer(id: "%s") { username } }
//        """.formatted(customerId);
//
//    String checkResponse = executeGraphQLQuery(query, adminClient);
//    assertThat(checkResponse).contains("null");
//  }

  /**
   * Extrahiert die ID aus der GraphQL-Antwort.
   *
   * @param response JSON-Antwort
   * @return Extrahierte ID
   */
  private String extractIdFromResponse(String response) {
    // Simpler JSON-Parser für Tests (idealerweise mit Jackson oder Gson ersetzen)
    int startIndex = response.indexOf("\"id\":\"") + 6;
    int endIndex = response.indexOf("\"", startIndex);
    return response.substring(startIndex, endIndex);
  }

  /**
   * Konvertiert eine GraphQL-Antwort in ein `TestCustomer`-Objekt.
   *
   * @param response JSON-Antwort
   * @return `TestCustomer`-Objekt
   */
//  private TestCustomer parseCustomerFromResponse(String response) {
//    // Simpler JSON-Parser für Tests (idealerweise mit Jackson oder Gson ersetzen)
//    Map<String, Object> customerMap = new HashMap<>();
//    customerMap.put("username", extractField(response, "username"));
//    customerMap.put("email", extractField(response, "email"));
//    customerMap.put("tierLevel", Integer.parseInt(extractField(response, "tierLevel")));
//
//    return new TestCustomer(customerMap);
//  }

  /**
   * Extrahiert ein Feld aus der JSON-Antwort.
   *
   * @param response JSON-String
   * @param fieldName Name des Feldes
   * @return Wert des Feldes als String
   */
  private String extractField(String response, String fieldName) {
    int startIndex = response.indexOf("\"" + fieldName + "\":\"") + fieldName.length() + 3;
    int endIndex = response.indexOf("\"", startIndex);
    return response.substring(startIndex, endIndex);
  }
}
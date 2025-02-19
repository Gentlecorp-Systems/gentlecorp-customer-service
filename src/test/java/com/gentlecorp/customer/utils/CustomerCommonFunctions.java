package com.gentlecorp.customer.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
import com.gentlecorp.customer.model.CustomGraphQLError;
import com.gentlecorp.customer.model.GraphQlResponse;
import com.gentlecorp.customer.model.entity.Address;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.testData.TestData;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(Env.class)
public class CustomerCommonFunctions extends TestData {

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

  protected GraphQlResponse<Customer> executeCreateCustomerGraphQLQuery(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return client.mutate().build().document(query)
        .variables(variables)
        .retrieve("createCustomer") // Hier wird sichergestellt, dass nur das gewünschte Feld extrahiert wird
        //.toEntity(new ParameterizedTypeReference<GraphQlResponse<Customer>>() {}) // Richtige Typisierung
        .toEntity(Customer.class) // In ein Customer-Objekt umwandeln
        .map(customer -> {
          log.debug("executeCreateCustomerGraphQLQuery: Extracted customer: {}", customer);
          return new GraphQlResponse<>(customer, List.of());
        }) // Erfolgreiche Antwort
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


  protected GraphQlResponse<Customer> executeCreateCustomerGraphQLQuery2(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return (GraphQlResponse<Customer>) client.document(query)
        .variables(variables)
        .execute()
        .map(response -> {
          log.debug("GraphQL Full Response: {}", response);

          // Prüfe, ob die Antwort Errors enthält
          if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            log.error("GraphQL Errors gefunden: {}", response.getErrors());

            String errorMessage = response.getErrors().toString();
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

              return new GraphQlResponse<>(null, List.of(
                  new CustomGraphQLError(extractedMessage, locations, null, extensions)
              ));
            } catch (Exception ex) {
              log.error("Fehler beim Parsen des GraphQL-Fehlers: {}", ex.getMessage());
            }

            return new GraphQlResponse<>(null, List.of(new CustomGraphQLError(response.getErrors().toString())));
          }

          // Prüfe, ob `createCustomer` existiert
          Map<String, Object> data = response.getData();
          if (data != null && data.containsKey("createCustomer")) {
            Customer customer = new ObjectMapper().convertValue(data.get("createCustomer"), Customer.class);
            return new GraphQlResponse<>(customer, List.of());
          } else {
            log.error("Kein createCustomer Feld in der Antwort gefunden: {}", data);
            return new GraphQlResponse<>(null, List.of(new CustomGraphQLError("Kein Kunde wurde erstellt")));
          }
        })
        .onErrorResume(error -> {
          log.error("GraphQL Client Exception: {}", error.getMessage());
          return Mono.just(new GraphQlResponse<>(null, List.of(new CustomGraphQLError(error.getMessage()))));
        })
        .block();
  }

  protected String executeUpdateCustomerGraphQLQuery(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return client.mutate().build().document(query)
        .variables(variables)
        .retrieve("updateContact") // Hier wird sichergestellt, dass nur das gewünschte Feld extrahiert wird
        //.toEntity(new ParameterizedTypeReference<GraphQlResponse<Customer>>() {}) // Richtige Typisierung
        .toEntity(String.class) // In ein Customer-Objekt umwandeln
        .block();
  }
  protected GraphQlResponse<Customer> executeUpdateCustomerGraphQLQuery2(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return (GraphQlResponse<Customer>) client.document(query)
        .variables(variables)
        .execute()
        .map(response -> {
          log.debug("GraphQL Full Response: {}", response);

          // Prüfe, ob die Antwort Errors enthält
          if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            log.error("GraphQL Errors gefunden: {}", response.getErrors());

            String errorMessage = response.getErrors().toString();
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

              return new GraphQlResponse<>(null, List.of(
                  new CustomGraphQLError(extractedMessage, locations, null, extensions)
              ));
            } catch (Exception ex) {
              log.error("Fehler beim Parsen des GraphQL-Fehlers: {}", ex.getMessage());
            }

            return new GraphQlResponse<>(null, List.of(new CustomGraphQLError(response.getErrors().toString())));
          }

          // Prüfe, ob `createCustomer` existiert
          Map<String, Object> data = response.getData();
          if (data != null && data.containsKey("createCustomer")) {
            Customer customer = new ObjectMapper().convertValue(data.get("createCustomer"), Customer.class);
            return new GraphQlResponse<>(customer, List.of());
          } else {
            log.error("Kein createCustomer Feld in der Antwort gefunden: {}", data);
            return new GraphQlResponse<>(null, List.of(new CustomGraphQLError("Kein Kunde wurde erstellt")));
          }
        })
        .onErrorResume(error -> {
          log.error("GraphQL Client Exception: {}", error.getMessage());
          return Mono.just(new GraphQlResponse<>(null, List.of(new CustomGraphQLError(error.getMessage()))));
        })
        .block();
  }

  protected GraphQlResponse<Customer> executeDeleteCustomerGraphQLQuery(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return client.document(query)
        .variables(variables)
        .retrieve("deleteCustomer") // Hier wird sichergestellt, dass nur das gewünschte Feld extrahiert wird
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

  protected GraphQlResponse<Customer> executeDeleteCustomerGraphQLQuery2(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return (GraphQlResponse<Customer>) client.document(query)
        .variables(variables)
        .execute()
        .map(response -> {
          log.debug("GraphQL Full Response: {}", response);

          // Prüfe, ob die Antwort Errors enthält
          if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            log.error("GraphQL Errors gefunden: {}", response.getErrors());

            String errorMessage = response.getErrors().toString();
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

              return new GraphQlResponse<>(null, List.of(
                  new CustomGraphQLError(extractedMessage, locations, null, extensions)
              ));
            } catch (Exception ex) {
              log.error("Fehler beim Parsen des GraphQL-Fehlers: {}", ex.getMessage());
            }

            return new GraphQlResponse<>(null, List.of(new CustomGraphQLError(response.getErrors().toString())));
          }

          // Prüfe, ob `createCustomer` existiert
          Map<String, Object> data = response.getData();
          if (data != null && data.containsKey("createCustomer")) {
            Customer customer = new ObjectMapper().convertValue(data.get("createCustomer"), Customer.class);
            return new GraphQlResponse<>(customer, List.of());
          } else {
            log.error("Kein createCustomer Feld in der Antwort gefunden: {}", data);
            return new GraphQlResponse<>(null, List.of(new CustomGraphQLError("Kein Kunde wurde erstellt")));
          }
        })
        .onErrorResume(error -> {
          log.error("GraphQL Client Exception: {}", error.getMessage());
          return Mono.just(new GraphQlResponse<>(null, List.of(new CustomGraphQLError(error.getMessage()))));
        })
        .block();
  }

  protected UUID executeAddContactGraphQLQuery(
      final String query, final Map<String, Object> variables, final HttpGraphQlClient client
  ) {
    return client.mutate().build().document(query)
        .variables(variables)
        .retrieve("addContact") // Hier wird sichergestellt, dass nur das gewünschte Feld extrahiert wird
        .toEntity(UUID.class) // In ein Customer-Objekt umwandeln
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
  protected UUID createCustomer(String username, String email, int tierLevel) {
    final var client = testClientProvider.getVisitorClient();

    // Adresse als separate Map definieren
    final Map<String, Object> address = new HashMap<>();
    address.put("street", NEW_USER_STREET);
    address.put("houseNumber", NEW_USER_HOUSE_NUMBER);
    address.put("zipCode", NEW_USER_ZIP_CODE);
    address.put("city", NEW_USER_CITY);
    address.put("state", NEW_USER_STATE);
    address.put("country", NEW_USER_COUNTRY);

    // Input-Daten für die GraphQL-Anfrage
    final Map<String, Object> input = new HashMap<>();
    input.put(LAST_NAME, NEW_USER_LAST_NAME);
    input.put(FIRST_NAME, NEW_USER_FIRST_NAME);
    input.put(EMAIL, email);
    input.put(USERNAME, username);
    input.put(TIER_LEVEL, tierLevel);
    input.put(SUBSCRIBED, true);
    input.put(BIRTHDATE, NEW_USER_BIRTH_DATE);
    input.put(GENDER, NEW_USER_GENDER);
    input.put(MARITAL_STATUS, NEW_USER_MARITAL_STATUS);
    input.put(PHONE_NUMBER, NEW_USER_PHONE_NUMBER);
    input.put(ADDRESS, address);
    input.put(CONTACT_OPTIONS, List.of(NEW_USER_CONTACT_OPTIONS));
    input.put(INTERESTS, List.of(NEW_USER_INTERESTS));

    // Haupt-Variablen-Map für die GraphQL-Query
    final Map<String, Object> variables = new HashMap<>();
    variables.put("input", input);
    variables.put(PASSWORD, NEW_USER_PASSWORD);

    final var response = executeCreateCustomerGraphQLQuery(customerCreateQuery, variables, client);
    log.debug("createCustomer: Response: {}", response.getData());
    final var newCustomer = response.getData();
    verifyCustomerDetails(newCustomer, variables);

    return newCustomer.getId();
  }

  private void verifyCustomerDetails(Customer customer, final Map<String, Object> variables) {
    log.debug("verifyCustomerDetails: variable: " + variables);
    log.debug("verifyCustomerDetails: customer: " + customer);
    log.debug("verifyCustomerDetails: address: " + customer.getAddress());

    final var variable = ((Map<String, Object>) variables.get("input"));
    assertThat(customer).isNotNull();
    assertThat(customer.getId()).isNotNull();
    assertThat(customer.getVersion()).isEqualTo(1);
    assertThat(customer.getFirstName()).isEqualTo(variable.get(FIRST_NAME));
    assertThat(customer.getLastName()).isEqualTo(variable.get(LAST_NAME));
    assertThat(customer.getEmail()).isEqualTo(variable.get(EMAIL));
    assertThat(customer.getPhoneNumber()).isEqualTo(variable.get(PHONE_NUMBER));
    assertThat(customer.getUsername()).isEqualTo(variable.get(USERNAME));
    assertThat(customer.getTierLevel()).isEqualTo(variable.get(TIER_LEVEL));
    assertThat(customer.isSubscribed()).isTrue();
    assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse((CharSequence) variable.get(BIRTHDATE), DateTimeFormatter.ISO_LOCAL_DATE));
    assertThat(customer.getGender().name()).isEqualTo(variable.get(GENDER));
    assertThat(customer.getMaritalStatus().name()).isEqualTo(variable.get(MARITAL_STATUS));

    verifyAddress(customer.getAddress());
    verifyInterestsAndContactOptions(customer);
  }

  protected void verifyCustomerAsAdmin(UUID customerId, String expectedUsername, final String expectedEmail, int expectedTierLevel) {
    final var client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
    final Map<String, Object> id = Map.of("id", customerId);
    final var response = executeCustomerGraphQLQuery(fullCustomerQuery, id, client);
    final var createdCustomer = response.getData();

    log.debug("Response: {}", createdCustomer);

    assertThat(createdCustomer).isNotNull();
    verifyCustomerDetails(createdCustomer, customerId, expectedUsername, expectedEmail, expectedTierLevel);
  }

  void verifyCustomerDetails(final Customer customer, final UUID customerId, final String expectedUsername, final String expectedEmail, int expectedTierLevel) {
    log.debug("verifyCustomerDetails2: customer: " + customer);
    log.debug("verifyCustomerDetails2: address: " + customer.getAddress());

    assertThat(customer).isNotNull();
    assertThat(customer.getId()).isEqualTo(customerId);
    assertThat(customer.getVersion()).isEqualTo(1);
    assertThat(customer.getFirstName()).isEqualTo(NEW_USER_FIRST_NAME);
    assertThat(customer.getLastName()).isEqualTo(NEW_USER_LAST_NAME);
    assertThat(customer.getEmail()).isEqualTo(expectedEmail);
    assertThat(customer.getPhoneNumber()).isEqualTo(NEW_USER_PHONE_NUMBER);
    assertThat(customer.getUsername()).isEqualTo(expectedUsername);
    assertThat(customer.getTierLevel()).isEqualTo(expectedTierLevel);
    assertThat(customer.isSubscribed()).isTrue();
    assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse(NEW_USER_BIRTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE));
    assertThat(customer.getGender().name()).isEqualTo(NEW_USER_GENDER);
    assertThat(customer.getMaritalStatus().name()).isEqualTo(NEW_USER_MARITAL_STATUS);

    verifyAddress(customer.getAddress());
    verifyInterestsAndContactOptions(customer);
  }

  protected void verifyAccessRights(final UUID customerId) {
    HttpGraphQlClient client;
    Map<String, Object> id;
    GraphQlResponse<Customer> response;

    client = testClientProvider.getAuthenticatedClient(USER_SUPREME);
    id = Map.of("id", customerId);
    response = executeCustomerGraphQLQuery(customerQuery, id, client);
    assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("FORBIDDEN");

    client = testClientProvider.getAuthenticatedClient(USER_ELITE);
    id = Map.of("id", customerId);
    response = executeCustomerGraphQLQuery(customerQuery, id, client);
    assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("FORBIDDEN");

    client = testClientProvider.getAuthenticatedClient(USER_BASIC);
    id = Map.of("id", customerId);
    response = executeCustomerGraphQLQuery(customerQuery, id, client);
    assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("FORBIDDEN");
  }

  protected void createAndVerifyCustomer(final UUID originalCustomerId, String username, final String expectedEmail,  int expectedTierLevel) {
    var newCustomerClient = testClientProvider.createAuthenticatedClient(username, NEW_USER_PASSWORD);
    final Map <String,Object> id = Map.of("id", originalCustomerId);
    final var response = executeCustomerGraphQLQuery(fullCustomerQuery, id, newCustomerClient);

    assertThat(response.getData()).isNotNull();

    var createdCustomer = response.getData();
    verifyCustomerDetails(createdCustomer, originalCustomerId, username, expectedEmail, expectedTierLevel);

    verifyAddress(createdCustomer.getAddress());
    verifyInterestsAndContactOptions(createdCustomer);
  }

  protected void deleteAndVerifyCustomer(final UUID customerId, final int version) {
    HttpGraphQlClient client;
    Map<String, Object> id;
    GraphQlResponse<Customer> response;

    client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
    id = Map.of("id", customerId, "version", version);
    response = executeDeleteCustomerGraphQLQuery(customersDeleteQuery, id, client);

    client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
    id = Map.of("id", customerId);
    response = executeCustomerGraphQLQuery(customerQuery, id, client);
    assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("NOT_FOUND");
  }

  protected void verifyAddress(final Address address) {
    log.debug("verifyAddress: address={}", address);

    assertThat(address).isNotNull();

    assertThat(address).satisfies(addr -> {
      assertThat(addr.getStreet()).isEqualTo(NEW_USER_STREET);
      assertThat(addr.getHouseNumber()).isEqualTo(NEW_USER_HOUSE_NUMBER);
      assertThat(addr.getZipCode()).isEqualTo(NEW_USER_ZIP_CODE);
      assertThat(addr.getCity()).isEqualTo(NEW_USER_CITY);
      assertThat(addr.getState()).isEqualTo(NEW_USER_STATE);
      assertThat(addr.getCountry()).isEqualTo(NEW_USER_COUNTRY);
    });
  }

  protected void verifyInterestsAndContactOptions(Customer customer) {
    assertThat(customer.getInterests())
        .allMatch(interest -> interest.name().equals(NEW_USER_INTERESTS));

    assertThat(customer.getContactOptions())
        .allMatch(contactOption -> contactOption.name().equalsIgnoreCase(NEW_USER_CONTACT_OPTIONS));
  }

  protected Map<String, Object> createBaseCustomerInput() {
    // Adresse als separate Map definieren
    final Map<String, Object> address = new HashMap<>();
    address.put("street", NEW_USER_STREET);
    address.put("houseNumber", NEW_USER_HOUSE_NUMBER);
    address.put("zipCode", NEW_USER_ZIP_CODE);
    address.put("city", NEW_USER_CITY);
    address.put("state", NEW_USER_STATE);
    address.put("country", NEW_USER_COUNTRY);

    // Input-Daten für die GraphQL-Anfrage
    final Map<String, Object> input = new HashMap<>();
    input.put(LAST_NAME, NEW_USER_LAST_NAME);
    input.put(FIRST_NAME, NEW_USER_FIRST_NAME);
    input.put(EMAIL, BASIC_EMAIL);
    input.put(USERNAME, BASIC_USERNAME);
    input.put(TIER_LEVEL, TIER_LEVEL_1);
    input.put(SUBSCRIBED, true);
    input.put(BIRTHDATE, NEW_USER_BIRTH_DATE);
    input.put(GENDER, NEW_USER_GENDER);
    input.put(MARITAL_STATUS, NEW_USER_MARITAL_STATUS);
    input.put(PHONE_NUMBER, NEW_USER_PHONE_NUMBER);
    input.put(ADDRESS, address);
    input.put(CONTACT_OPTIONS, List.of(NEW_USER_CONTACT_OPTIONS));
    input.put(INTERESTS, List.of(NEW_USER_INTERESTS));

    return input;
  }

  protected Map<String, Object> createUpdateRequestBody() {
    Map<String, Object> updateRequest = new HashMap<>();

    updateRequest.put(LAST_NAME, UPDATED_LAST_NAME);
    updateRequest.put(FIRST_NAME, UPDATED_FIRST_NAME);
    //updateRequest.put(USERNAME, UPDATED_USERNAME);
    updateRequest.put(EMAIL, UPDATED_EMAIL);
    updateRequest.put(PHONE_NUMBER, UPDATED_PHONE_NUMBER);
    updateRequest.put(TIER_LEVEL, UPDATED_TIER_LEVEL);
    updateRequest.put(SUBSCRIBED, UPDATED_SUBSCRIBED);
    //updateRequest.put(BIRTHDATE, UPDATED_BIRTH_DATE);
    //updateRequest.put(GENDER, UPDATED_GENDER);
    updateRequest.put(MARITAL_STATUS, UPDATED_MARITAL_STATUS);
    updateRequest.put(INTERESTS, List.of(UPDATED_INTEREST));
    updateRequest.put(CONTACT_OPTIONS, List.of(UPDATED_CONTACT_OPTION));

    Map<String, Object> address = new HashMap<>();
    address.put(STREET, UPDATED_STREET);
    address.put(HOUSE_NUMBER, UPDATED_HOUSE_NUMBER);
    address.put(ZIP_CODE, UPDATED_ZIP_CODE);
    address.put(CITY, UPDATED_CITY);
    address.put(STATE, UPDATED_STATE);
    address.put(COUNTRY, UPDATED_COUNTRY);
    updateRequest.put(ADDRESS, address);

    return updateRequest;
  }

  protected Customer createNewCustomer() {
    final Map<String, Object> input = createBaseCustomerInput();
    final Map<String, Object> variables = Map.of("input", input, PASSWORD, NEW_USER_PASSWORD);
    final var client = testClientProvider.getVisitorClient();
    final var response = executeCreateCustomerGraphQLQuery(customerCreateQuery, variables, client);
    return response.getData();
  }

  protected void verifyUpdatedCustomer(Customer customer) {
    assertThat(customer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    assertThat(customer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
    assertThat(customer.getEmail()).isEqualTo(UPDATED_EMAIL);
    assertThat(customer.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    assertThat(customer.getTierLevel()).isEqualTo(UPDATED_TIER_LEVEL);
    assertThat(customer.isSubscribed()).isEqualTo(UPDATED_SUBSCRIBED);
    //assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse(UPDATED_BIRTH_DATE));
    //assertThat(customer.getGender().name()).isEqualToIgnoringCase(UPDATED_GENDER);
    assertThat(customer.getMaritalStatus().name()).isEqualToIgnoringCase(UPDATED_MARITAL_STATUS);
//    assertThat(customer.interests()).hasSize(1);
//    assertThat(customer.interests().getFirst().getInterest()).isEqualToIgnoringCase(UPDATED_INTEREST);
//    assertThat(customer.contactOptions()).hasSize(1);
//    assertThat(customer.contactOptions().getFirst().getOption()).isEqualToIgnoringCase(UPDATED_CONTACT_OPTION);

//    AddressDTO address = customer.address();
//    assertThat(address.street()).isEqualTo(UPDATED_STREET);
//    assertThat(address.houseNumber()).isEqualTo(UPDATED_HOUSE_NUMBER);
//    assertThat(address.zipCode()).isEqualTo(UPDATED_ZIP_CODE);
//    assertThat(address.city()).isEqualTo(UPDATED_CITY);
//    assertThat(address.state()).isEqualTo(UPDATED_STATE);
//    assertThat(address.country()).isEqualTo(UPDATED_COUNTRY);
  }
}
package com.gentlecorp.customer.test;

import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
import com.gentlecorp.customer.model.GraphQlResponse;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.utils.CustomerCommonFunctions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.HttpGraphQlClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDeleteCustomer extends CustomerCommonFunctions {
  private static final Logger log = LoggerFactory.getLogger(TestCreateCustomer.class);

  @BeforeAll
  protected void setup() {
    new Env();
    testClientProvider = new TestClientProvider();
    testClientProvider.init(serverPort);
  }

  @Test()
  @DisplayName("Lösche einen Kunden als der Kunde")
  void testDeleteNewCustomerAsCustomer() {
      HttpGraphQlClient client;
      Map<String, Object> id;
      GraphQlResponse<Customer> response;

      final var customer = createNewCustomer();

      client = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);
      id = Map.of("id", customer.getId(), "version", customer.getVersion());
      response = executeDeleteCustomerGraphQLQuery2(customersDeleteQuery, id, client);
      assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("FORBIDDEN");

      client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
      id = Map.of("id", customer.getId(), "version", customer.getVersion());
      executeDeleteCustomerGraphQLQuery(customersDeleteQuery, id, client);
    }

  @Test()
  @DisplayName("Lösche einen Kunden als Admin")
  void testDeleteNewCustomerAsAdmin() {
      HttpGraphQlClient client;
      Map<String, Object> id;
      GraphQlResponse<Customer> response;

      final var customer = createNewCustomer();

      client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
      id = Map.of("id", customer.getId(), "version", customer.getVersion());
      executeDeleteCustomerGraphQLQuery(customersDeleteQuery, id, client);

      client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
      id = Map.of("id", customer.getId());
      response = executeCustomerGraphQLQuery(customerQuery, id, client);
      assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("NOT_FOUND");
    }

    @ParameterizedTest(name = "Lösche neuer Kunde als {0}")
    @CsvSource({
        USER_USER + "," + USER,
        USER_SUPREME + "," + SUPREME,
        USER_BASIC + "," + BASIC,
        USER_ELITE + "," + ELITE
    })
    void testDeleteNewCustomerAsUser(final String user, final String role) {
      HttpGraphQlClient client;
      Map<String, Object> id;

      final var customer = createNewCustomer();

      client = testClientProvider.getAuthenticatedClient(user);
      id = Map.of("id", customer.getId(), "version", customer.getVersion());
      final var response = executeDeleteCustomerGraphQLQuery2(customersDeleteQuery, id, client);

      assertThat(response.getErrors().getFirst().getExtensions().get("classification")).isEqualTo("FORBIDDEN");

      client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
      id = Map.of("id", customer.getId(), "version", customer.getVersion());
      executeDeleteCustomerGraphQLQuery(customersDeleteQuery, id, client);
  }

  @ParameterizedTest(name = "Lösche neuer Kunde mit ungültiger version: {0}")
  @CsvSource({
      "0,Die Versionsnummer 0 ist veraltet.",
      "2,Die angegebene Version 2 ist voraus und noch nicht gültig."
  })
  void testDeleteNewCustomerInvalidVersion(final int version, final String message) {
    HttpGraphQlClient client;
    Map<String, Object> id;
    GraphQlResponse<Customer> response;

    final var customer = createNewCustomer();

    client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
    id = Map.of("id", customer.getId(), "version", version);
    response = executeDeleteCustomerGraphQLQuery2(customersDeleteQuery, id, client);

      final var firstError = response.getErrors().getFirst();
      assertThat(firstError.getMessage()).isEqualTo(message);
      assertThat(firstError.getExtensions().get("classification")).isEqualTo("PRECONDITION_FAILED");

    client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
    id = Map.of("id", customer.getId(), "version", customer.getVersion());
    executeDeleteCustomerGraphQLQuery(customersDeleteQuery, id, client);
  }

}

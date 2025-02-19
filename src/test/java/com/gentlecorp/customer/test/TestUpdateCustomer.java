package com.gentlecorp.customer.test;

import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUpdateCustomer extends CustomerCommonFunctions {
  private static final Logger log = LoggerFactory.getLogger(TestCreateCustomer.class);

  @BeforeAll
  protected void setup() {
    new Env();
    testClientProvider = new TestClientProvider();
    testClientProvider.init(serverPort);
  }


  @Nested
  @DisplayName("Tests für das Aktualisieren von Kunden")
  class UpdateTests {

    @Test()
    @DisplayName("Aktualisiere alle attribute eines Kunden als admin")
    void testUpdateNewCustomerAsAdmin() {
      final var customer = createNewCustomer();
      final var updateRequest = createUpdateRequestBody();
      final var input = Map.of(
          "input",updateRequest,
          "id",customer.getId(),
          "version","1"
      );

        performUpdateAndAssertResponse(input);
        verifyCustomerUpdate(customer.getId());
        deleteAndVerifyCustomer(customer.getId(), 2);
    }

    @ParameterizedTest(name = "Aktualisiere neuer Kunde als {0}")
    @CsvSource({
        ROLE_USER + "," + USER,
        ROLE_SUPREME + "," + SUPREME,
        ROLE_BASIC + "," + BASIC,
        ROLE_ELITE + "," + ELITE
    })
    void testUpdateNewCustomerNotAdmin(final String user, final String role) {
      final var customer = createNewCustomer();
      final var updateRequest = createUpdateRequestBody();
      final var input = Map.of(
          "input",updateRequest,
          "id",customer.getId(),
          "version","1"
      );

      final var client = testClientProvider.getAuthenticatedClient(user);
      final var response = executeUpdateCustomerGraphQLQuery2(customerUpdateQuery, input, client);
      final var errors = response.getErrors();
      final var error = errors.getFirst();

      assertThat(response.getData()).isNull();
      assertThat(errors).isNotNull();
      assertThat(error).isNotNull();
      assertThat(error.getMessage()).isEqualTo(String.format("Zugriff verweigert: Benutzer '%s' besitzt nur die Rollen [%s], die für diese Anfrage nicht ausreichen.", user, role));
      assertThat(error.getExtensions().get("classification")).isEqualTo("FORBIDDEN");
      deleteAndVerifyCustomer(customer.getId(), 1);
    }

    @Test()
    @DisplayName("Aktualisiere alle attribute eines Kunden als der Kunde")
    void testUpdateNewCustomerAsCustomer() {
      final var customer = createNewCustomer();
      final var updateRequest = createUpdateRequestBody();
      final var input = Map.of(
          "input",updateRequest,
          "id",customer.getId(),
          "version","1"
      );

      final var client = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);
      executeUpdateCustomerGraphQLQuery(customerUpdateQuery, input, client);
      verifyCustomerUpdate(customer.getId());
      deleteAndVerifyCustomer(customer.getId(), 2);
    }

    static Stream<Arguments> updatedAttributes() {
      return Stream.of(
          Arguments.of(false,LAST_NAME, UPDATED_LAST_NAME),
          Arguments.of(false,FIRST_NAME, UPDATED_FIRST_NAME),
          Arguments.of(false,EMAIL, UPDATED_EMAIL),
          Arguments.of(false,PHONE_NUMBER, UPDATED_PHONE_NUMBER),
          Arguments.of(false,TIER_LEVEL,UPDATED_TIER_LEVEL),
          Arguments.of(false,SUBSCRIBED, UPDATED_SUBSCRIBED),
          Arguments.of(false,MARITAL_STATUS, UPDATED_MARITAL_STATUS),
          Arguments.of(false,CONTACT_OPTIONS, UPDATED_CONTACT_OPTION),
          Arguments.of(false,INTERESTS, UPDATED_INTEREST),
          Arguments.of(true,STREET,UPDATED_STREET),
          Arguments.of(true,HOUSE_NUMBER,UPDATED_HOUSE_NUMBER),
          Arguments.of(true,ZIP_CODE, UPDATED_ZIP_CODE),
          Arguments.of(true,CITY, UPDATED_CITY),
          Arguments.of(true,STATE, UPDATED_STATE),
          Arguments.of(true,COUNTRY, UPDATED_COUNTRY)
      );
    }

    @ParameterizedTest(name = "Aktualisiere {1}-attribut eines Kunden als admin")
    @MethodSource("updatedAttributes")
    void testUpdateNewCustomerAttributeAsAdmin(final boolean isAddressAttribute, final String attributeName, final Object attributeValue) {
      final var customer = createNewCustomer();

      Map<String, Object> updateRequest = new HashMap<>();

      if (isAddressAttribute) {
        updateRequest.put(ADDRESS, Map.of(attributeName,attributeValue));
      }
      else {
        updateRequest.put(attributeName, attributeValue);
      }

      Map<String, Object> address = new HashMap<>();
      address.put(STREET, UPDATED_STREET);
      address.put(HOUSE_NUMBER, UPDATED_HOUSE_NUMBER);
      address.put(ZIP_CODE, UPDATED_ZIP_CODE);
      address.put(CITY, UPDATED_CITY);
      address.put(STATE, UPDATED_STATE);
      address.put(COUNTRY, UPDATED_COUNTRY);
      updateRequest.put(ADDRESS, address);

      final var input = Map.of(
          "input",updateRequest,
          "id",customer.getId(),
          "version","1"
      );

      performUpdateAndAssertResponse(input);

      final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
      final Map<String, Object> variables = Map.of(
          "id",  customer.getId()
      );
      final var response = executeCustomerGraphQLQuery(fullCustomerQuery, variables, client);

      final var updatedCustomer = response.getData();
      assertThat(updatedCustomer).isNotNull();

      assertCustomerAttribute(isAddressAttribute, attributeName, updatedCustomer);

      deleteAndVerifyCustomer(customer.getId(), 2);
    }
  }

  private void assertCustomerAttribute(final boolean isAddressAttribute, final String attributeName, final Customer customer) {
    if (isAddressAttribute) {
      ATTRIBUTE_ASSERTIONS_ADDRESS.getOrDefault(attributeName,
          c -> {throw new IllegalArgumentException("Unbekanntes Adress Attribut: " + attributeName);
      }).accept(customer);
    } else {
    ATTRIBUTE_ASSERTIONS.getOrDefault(attributeName,
        c -> { throw new IllegalArgumentException("Unbekanntes Attribut: " + attributeName); }
    ).accept(customer);
  }
  }

  private void verifyCustomerUpdate(final UUID customerId) {
    final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
    final Map<String, Object> variables = Map.of(
        "id",  customerId
    );
    final var response = executeCustomerGraphQLQuery(fullCustomerQuery, variables, client);

    final var customer = response.getData();
    assertThat(customer).isNotNull();
    verifyUpdatedCustomer(customer);
  }

  private void performUpdateAndAssertResponse(final Map<String, Object> variables) {
    final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
    executeUpdateCustomerGraphQLQuery(customerUpdateQuery, variables, client);
  }

  private static final Map<String, Consumer<Customer>> ATTRIBUTE_ASSERTIONS = Map.of(
      LAST_NAME, customer -> assertThat(customer.getLastName()).isEqualTo(UPDATED_LAST_NAME),
      FIRST_NAME, customer -> assertThat(customer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME),
      EMAIL, customer -> assertThat(customer.getEmail()).isEqualTo(UPDATED_EMAIL),
      PHONE_NUMBER, customer -> assertThat(customer.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER),
      TIER_LEVEL, customer -> assertThat(customer.getTierLevel()).isEqualTo(UPDATED_TIER_LEVEL),
      SUBSCRIBED, customer -> assertThat(customer.isSubscribed()).isEqualTo(UPDATED_SUBSCRIBED),
      MARITAL_STATUS, customer -> assertThat(customer.getMaritalStatus().name()).isEqualTo(UPDATED_MARITAL_STATUS),
      CONTACT_OPTIONS, customer -> assertThat(customer.getContactOptions().getFirst().name()).isEqualTo(UPDATED_CONTACT_OPTION),
      INTERESTS, customer -> assertThat(customer.getInterests().getFirst().name()).isEqualTo(UPDATED_INTEREST)
  );

  private static final Map<String, Consumer<Customer>> ATTRIBUTE_ASSERTIONS_ADDRESS = Map.of(
      STREET, customer -> assertThat(customer.getAddress().getStreet()).isEqualTo(UPDATED_STREET),
      HOUSE_NUMBER, customer -> assertThat(customer.getAddress().getHouseNumber()).isEqualTo(UPDATED_HOUSE_NUMBER),
      ZIP_CODE, customer -> assertThat(customer.getAddress().getZipCode()).isEqualTo(UPDATED_ZIP_CODE),
      CITY, customer -> assertThat(customer.getAddress().getCity()).isEqualTo(UPDATED_CITY),
      STATE, customer -> assertThat(customer.getAddress().getState()).isEqualTo(UPDATED_STATE),
      COUNTRY, customer -> assertThat(customer.getAddress().getCountry()).isEqualTo(UPDATED_COUNTRY)
  );
}

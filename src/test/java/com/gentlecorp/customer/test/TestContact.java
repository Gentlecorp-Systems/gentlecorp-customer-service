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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestContact extends CustomerCommonFunctions {
    private static final Logger log = LoggerFactory.getLogger(TestContact.class);

    @BeforeAll
    protected void setup() {
        new Env();
        testClientProvider = new TestClientProvider();
        testClientProvider.init(serverPort);
    }

    private Map<String, Object> createContactRequestBody() {
        Map<String, Object> updateRequest = new HashMap<>();

        updateRequest.put(LAST_NAME, NEW_CONTACT_LAST_NAME);
        updateRequest.put(FIRST_NAME, NEW_CONTACT_FIRST_NAME);
        updateRequest.put(RELATIONSHIP, NEW_CONTACT_RELATIONSHIP);
        updateRequest.put(WITHDRAWAL_LIMIT, NEW_CONTACT_WITHDRAWAL_LIMIT);
        updateRequest.put(EMERGENCY_CONTACT, NEW_CONTACT_EMERGENCY);
        updateRequest.put(START_DATE, NEW_CONTACT_START_DATE);
        updateRequest.put(END_DATE, NEW_CONTACT_END_DATE);

        return updateRequest;
    }

    @Nested
    @DisplayName("Tests um Kontakte hinzuzufügen")
    class AddContactTests {

        @Test
        void testAddContact() {
            HttpGraphQlClient client;

            final var customer = createNewCustomer();
            final var contactInput = createContactRequestBody();
            final var input = Map.of(
                "input",contactInput,
                "id",customer.getId()
            );

            client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
            final var newContactId = executeAddContactGraphQLQuery(addContactQuery, input, client);

            client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
            final Map<String, Object> variables = Map.of(
                "id",  customer.getId()
            );
            final var response = executeCustomerGraphQLQuery(fullCustomerQuery, variables, client);
            assertThat(response).isNotNull();
            assertThat(response.getErrors()).isEmpty();

            final var customerDB = response.getData();
            assertThat(customerDB).isNotNull();

            final var contactIds = customerDB.getContactIds();
            assertThat(contactIds).isNotNull();
            assertThat(contactIds).isNotEmpty();
            assertThat(contactIds.size()).isEqualTo(1);
            assertThat(contactIds.getFirst()).isEqualTo(newContactId);
            assertThat(contactIds).contains(newContactId);

            deleteAndVerifyCustomer(customer.getId(), 2);
        }

        @ParameterizedTest(name = "Fehlgeschlagene Kundenerstellung mit ungültigem Wert für {0}")
        @CsvSource({
            LAST_NAME + ", " + INVALID_LAST_NAME + ",Der Nachname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.",
            FIRST_NAME + ", " + INVALID_FIRST_NAME + ",Der Vorname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.",
            // WITHDRAWAL_LIMIT + ", " + INVALID_CONTACT_WITHDRAWAL_LIMIT + ",Das Auszahlungslimit darf nicht negativ sein",
            START_DATE + ", " + INVALID_CONTACT_START_DATE + ",Das Startdatum darf nicht in der Vergangenheit liegen.",
            //END_DATE + ", " + INVALID_CONTACT_END_DATE + ",Das Enddatum darf nicht in der Vergangenheit liegen.",
        })
        void testFailCreateCustomerWithInvalidStringInput(
            final String attribut, final Object invalidInput, final String message) {
            HttpGraphQlClient client;
            Map<String, Object> id;
            GraphQlResponse<Customer> response;

            final var customer = createNewCustomer();
            final var contactInput = createContactRequestBody();
            contactInput.put(attribut, invalidInput);
            final var input = Map.of(
                "input",contactInput,
                "id",customer.getId()
            );

            client = testClientProvider.getAuthenticatedClient(USER_ADMIN);
            response = executeUpdateCustomerGraphQLQuery2(addContactQuery, input, client);

            final var firstError = response.getErrors().getFirst();
            assertThat(firstError.getMessage()).isEqualTo(String.format(message));
            assertThat(firstError.getExtensions().get("classification")).isEqualTo("BAD_REQUEST");

            deleteAndVerifyCustomer(customer.getId(), 1);

        }

        @Test
        void testAddInvalidContact() {}

        @Test
        void testAddDuplicateContact() {}

        @Test
        void testAddContactAsUser() {}
    }

    @Nested
    @DisplayName("Tests um Kontakte zu Aktualisieren")
    class UpdateContactTests {
        @Test
        void testUpdateContact() {}

        @Test
        void testUpdateInvalidContact() {}

        @Test
        void testUpdateDuplicateContact() {}

        @Test
        void testUpdateContactAsUser() {}

        @Test
        void testUpdateNonExistingContact() {}
    }

    @Nested
    @DisplayName("Tests um Kontakte entfernen")
    class RemoveContactTests {
        @Test
        void testRemoveContact() {}

        @Test
        void testRemoveContactAsUser() {}

        @Test
        void testRemoveNonExistingContact() {}
    }


    private void validateErrorResponse(Map<String, Object> variables, String expectedMessage) {
        final var client = testClientProvider.getVisitorClient();
        final var response = executeCreateCustomerGraphQLQuery2(customerCreateQuery, variables, client);

        assertThat(response.getData()).isNull(); // Kein Kunde zurückgegeben
        assertThat(response.getErrors()).isNotEmpty(); // Es gibt Fehler

        final var firstError = response.getErrors().getFirst();
        assertThat(firstError.getMessage()).isEqualTo(expectedMessage);
        assertThat(firstError.getExtensions().get("classification")).isEqualTo("BAD_REQUEST");
    }
}
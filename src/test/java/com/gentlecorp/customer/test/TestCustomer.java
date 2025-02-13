package com.gentlecorp.customer.test;

import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
import com.gentlecorp.customer.model.dto.AddressDTO;
import com.gentlecorp.customer.model.entity.Address;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.enums.StatusType;
import com.gentlecorp.customer.utils.CustomerCommonFunctions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCustomer extends CustomerCommonFunctions {


    private static final Logger log = LoggerFactory.getLogger(TestCustomer.class);

    @BeforeAll
    protected void setup() {
        new Env();
        testClientProvider = new TestClientProvider();
        testClientProvider.init(serverPort);
    }




    @Test
    void testGetAllCustomersByIds() {
        assertThat(testClientProvider).isNotNull();

        final var adminClient = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
        assertThat(adminClient).isNotNull();

        for (int i = 0; i <= 26; i++) {
            String customerId = String.format("00000000-0000-0000-0000-%012d", i);
            final String query = """
                query Customer($id: ID!) {
                    customer(id: $id) {
                        id
                    }
                }
                """;

            final Map<String, Object> id = Map.of(
                "id",  customerId
            );

            final var customer = executeCustomerGraphQLQuery(query, id, adminClient);
            assertThat(customer).isNotNull();
            assertThat(customer.getData().getId().toString()).isEqualTo(customerId);
           log.debug("Customer ID: {} successfully retrieved.", customerId);
        }
    }

    @Test
    void testGetHiroshiByIdAsAdmin() {
        final var adminClient = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

        final Map<String, Object> id = Map.of(
            "id",  ID_HIROSHI
        );

        final var customer = executeCustomerGraphQLQuery(customerQuery, id, adminClient);
        verifyHiroshiDetails(customer.getData());
    }

    @Test
    void testGetHiroshiByIdAsUser() {
        final var userClient = testClientProvider.getAuthenticatedClient(ROLE_USER);

        final Map<String, Object> id = Map.of(
            "id",  ID_HIROSHI
        );

        final var customer = executeCustomerGraphQLQuery(customerQuery, id, userClient);
        verifyHiroshiDetails(customer.getData());
    }
//
    @Test
    void testGetHiroshiByIdAsSupreme() {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_SUPREME);

        final Map<String, Object> id = Map.of("id", ID_HIROSHI);

        final var response = executeCustomerGraphQLQuery(customerQuery, id, client);

        assertThat(response.getData()).isNull(); // Kein Kunde zurückgegeben
        assertThat(response.getErrors()).isNotEmpty(); // Es gibt Fehler

        log.info(response.getErrors().getFirst().toSpecification().toString());

        final var firstError = response.getErrors().getFirst();
        assertThat(firstError.getMessage()).isEqualTo(String.format("Zugriff verweigert: Benutzer '%s' besitzt nur die Rollen [%s], die für diese Anfrage nicht ausreichen.",ROLE_SUPREME, SUPREME));
       assertThat(firstError.getExtensions().get("classification")).isEqualTo("FORBIDDEN");
    }


    @Test
    void testGetHiroshiByIdAsElite() {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_ELITE);

        final Map<String, Object> id = Map.of("id", ID_HIROSHI);

        final var response = executeCustomerGraphQLQuery(customerQuery, id, client);

        assertThat(response.getData()).isNull(); // Kein Kunde zurückgegeben
        assertThat(response.getErrors()).isNotEmpty(); // Es gibt Fehler

        log.info(response.getErrors().getFirst().toSpecification().toString());

        final var firstError = response.getErrors().getFirst();
        assertThat(firstError.getMessage()).isEqualTo(String.format("Zugriff verweigert: Benutzer '%s' besitzt nur die Rollen [%s], die für diese Anfrage nicht ausreichen.",ROLE_ELITE, ELITE));
        assertThat(firstError.getExtensions().get("classification")).isEqualTo("FORBIDDEN");
    }

    @Test
    void testGetHiroshiByIdAsBasic() {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_BASIC);

        final Map<String, Object> id = Map.of("id", ID_HIROSHI);

        final var response = executeCustomerGraphQLQuery(customerQuery, id, client);

        assertThat(response.getData()).isNull(); // Kein Kunde zurückgegeben
        assertThat(response.getErrors()).isNotEmpty(); // Es gibt Fehler

        log.info(response.getErrors().getFirst().toSpecification().toString());

        final var firstError = response.getErrors().getFirst();
        assertThat(firstError.getMessage()).isEqualTo(String.format("Zugriff verweigert: Benutzer '%s' besitzt nur die Rollen [%s], die für diese Anfrage nicht ausreichen.",ROLE_BASIC, BASIC));
        assertThat(firstError.getExtensions().get("classification")).isEqualTo("FORBIDDEN");
    }

    @Test
    void testGetHiroshiByIdAsVisitor() {
        final var client = testClientProvider.getVisitorClient();

        final Map<String, Object> id = Map.of("id", ID_HIROSHI);

        final var response = executeCustomerGraphQLQuery(customerQuery, id, client);

        assertThat(response.getData()).isNull(); // Kein Kunde zurückgegeben
        assertThat(response.getErrors()).isNotEmpty(); // Es gibt Fehler

        log.info(response.getErrors().getFirst().toSpecification().toString());

        final var firstError = response.getErrors().getFirst();
        assertThat(firstError.getMessage()).isEqualTo(("Unauthorized"));
    }

        @Test
    void testGetErikByIdAsErik() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_BASIC);
            final Map<String, Object> id = Map.of(
                "id",  ID_ERIK
            );
            final var customer = executeCustomerGraphQLQuery(customerQuery, id, client);
        verifyErikDetails(customer.getData());
    }

    @Test
    void testGetLeroyByIdAsLeroy() {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_ELITE);
        final Map<String, Object> id = Map.of(
            "id",  ID_LEROY
        );
        final var customer = executeCustomerGraphQLQuery(customerQuery, id, client);
        verifyLeroyDetails(customer.getData());
    }

    @Test
    void testGetCalebByIdAsCaleb() {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_SUPREME);
        final Map<String, Object> id = Map.of(
            "id",  ID_CALEB
        );
        final var customer = executeCustomerGraphQLQuery(customerQuery, id, client);
        verifyCalebDetails(customer.getData());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
        final String query = """
                query Customer($id: ID!) {
                    customer(id: $id) {
                        id
                    }
                }
                """;
        final Map<String, Object> id = Map.of(
            "id", NOT_EXISTING_ID
        );

        final var response = executeCustomerGraphQLQuery(query, id, client);
        assertThat(response.getData()).isNull(); // Kein Kunde zurückgegeben
        assertThat(response.getErrors()).isNotEmpty(); // Es gibt Fehler

        log.info(response.getErrors().getFirst().toSpecification().toString());

        final var firstError = response.getErrors().getFirst();
        assertThat(firstError.getMessage()).isEqualTo(String.format("Kein Kunde mit der ID %s gefunden.",NOT_EXISTING_ID));
        assertThat(firstError.getExtensions().get("classification")).isEqualTo("NOT_FOUND");
    }

        @Test
    void testGetFullHiroshiByIdAsAdmin() {
            final var adminClient = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            final Map<String, Object> id = Map.of(
                "id",  ID_HIROSHI
            );

            final var customer = executeCustomerGraphQLQuery(fullCustomerQuery, id, adminClient);
            verifyFullHiroshiDetails(customer.getData());
    }

    private void verifyHiroshiDetails(Customer customer) {
        assertThat(customer).isNotNull();
        assertThat(customer.getId().toString()).isEqualTo(ID_HIROSHI);
        assertThat(customer.getUsername()).isEqualTo(USERNAME_HIROSHI);
        assertThat(customer.getLastName()).isEqualTo(LAST_NAME_HIROSHI);
        assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME_HIROSHI);
        assertThat(customer.getEmail()).isEqualTo(EMAIL_HIROSHI);
        assertThat(customer.getPhoneNumber()).isEqualTo(PHONE_NUMBER_HIROSHI);
        assertThat(customer.isSubscribed()).isTrue();
        assertThat(customer.getTierLevel()).isEqualTo(TIER_LEVEL_1);
        assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_HIROSHI));
        assertThat(customer.getCustomerState()).isEqualTo(StatusType.ACTIVE);
        assertThat(customer.getGender().toString()).isEqualTo("MALE");
        assertThat(customer.getMaritalStatus()).isEqualTo(MaritalStatusType.MARRIED);
    }

        private void verifyFullHiroshiDetails(Customer customer) {
        verifyHiroshiDetails(customer);
        verifyHiroshiAddress(customer.getAddress());
        verifyHiroshiInterestsAndContactOptions(customer);
        //verifyHiroshiContacts(customer.contacts());
    }

    private void verifyHiroshiInterestsAndContactOptions(Customer customer) {
        assertThat(customer.getInterests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
        assertThat(customer.getContactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.PHONE);
    }

    private void verifyHiroshiAddress(Address address) {
        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo(STREET_HIROSHI);
        assertThat(address.getHouseNumber()).isEqualTo(HOUSE_NUMBER_HIROSHI);
        assertThat(address.getZipCode()).isEqualTo(ZIP_CODE_HIROSHI);
        assertThat(address.getCity()).isEqualTo(CITY_HIROSHI);
        assertThat(address.getState()).isEqualTo(STATE_HIROSHI);
        assertThat(address.getCountry()).isEqualTo(COUNTRY_HIROSHI);
    }

        private void verifyLeroyDetails(Customer customer) {
        assertThat(customer).isNotNull();
        assertThat(customer.getUsername()).isEqualTo(USERNAME_LEROY);
        assertThat(customer.getLastName()).isEqualTo(LAST_NAME_LEROY);
        assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME_LEROY);
        assertThat(customer.getEmail()).isEqualTo(EMAIL_LEROY);
        assertThat(customer.getPhoneNumber()).isEqualTo(PHONE_NUMBER_LEROY);
        assertThat(customer.isSubscribed()).isTrue();
        assertThat(customer.getTierLevel()).isEqualTo(TIER_LEVEL_2);
        assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_LEROY));
        assertThat(customer.getCustomerState()).isEqualTo(StatusType.ACTIVE);
        assertThat(customer.getGender()).isEqualTo(GenderType.MALE);
        assertThat(customer.getMaritalStatus()).isEqualTo(MaritalStatusType.SINGLE);
    }

    private void verifyErikDetails(Customer customer) {
        assertThat(customer).isNotNull();
        assertThat(customer.getUsername()).isEqualTo(USERNAME_ERIK);
        assertThat(customer.getLastName()).isEqualTo(LAST_NAME_ERIK);
        assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME_ERIK);
        assertThat(customer.getEmail()).isEqualTo(EMAIL_ERIK);
        assertThat(customer.getPhoneNumber()).isEqualTo(PHONE_NUMBER_ERIK);
        assertThat(customer.isSubscribed()).isFalse();
        assertThat(customer.getTierLevel()).isEqualTo(TIER_LEVEL_1);
        assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_ERIK));
        assertThat(customer.getCustomerState()).isEqualTo(StatusType.INACTIVE);
        assertThat(customer.getGender()).isEqualTo(GenderType.MALE);
        assertThat(customer.getMaritalStatus()).isEqualTo(MaritalStatusType.MARRIED);
    }

    private void verifyCalebDetails(Customer customer) {
        assertThat(customer).isNotNull();
        assertThat(customer.getUsername()).isEqualTo(USERNAME_CALEB);
        assertThat(customer.getLastName()).isEqualTo(LAST_NAME_CALEB);
        assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME_CALEB);
        assertThat(customer.getEmail()).isEqualTo(EMAIL_CALEB);
        assertThat(customer.getPhoneNumber()).isEqualTo(PHONE_NUMBER_CALEB);
        assertThat(customer.isSubscribed()).isTrue();
        assertThat(customer.getTierLevel()).isEqualTo(TIER_LEVEL_3);
        assertThat(customer.getBirthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_CALEB));
        assertThat(customer.getCustomerState()).isEqualTo(StatusType.ACTIVE);
        assertThat(customer.getGender()).isEqualTo(GenderType.MALE);
        assertThat(customer.getMaritalStatus()).isEqualTo(MaritalStatusType.MARRIED);
    }
}
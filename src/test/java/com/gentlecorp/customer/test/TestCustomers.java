package com.gentlecorp.customer.test;

import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
import com.gentlecorp.customer.model.CustomGraphQLError;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.Operator;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.gentlecorp.customer.model.enums.Operator.EQ;
import static com.gentlecorp.customer.model.enums.Operator.GTE;
import static com.gentlecorp.customer.model.enums.Operator.IN;
import static com.gentlecorp.customer.model.enums.Operator.LIKE;
import static com.gentlecorp.customer.model.enums.Operator.LTE;
import static com.gentlecorp.customer.model.enums.Operator.PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TestCustomers extends CustomerCommonFunctions {
    private static final Logger log = LoggerFactory.getLogger(TestCustomers.class);

    @BeforeAll
    protected void setup() {
        new Env();
        testClientProvider = new TestClientProvider();
        testClientProvider.init(serverPort);
    }

    @Nested
    @DisplayName("Tests für verschiedene Benutzerrollen")
    class UserRoleTests {

        @Test
        @DisplayName("Sollte alle Kunden für Admin zurückgeben")
        void testGetAllAsAdmin() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final var response = executeCustomersGraphQLQuery(customersQuery, null, client);
            assertValidCustomerResponse(response.getData());
        }

        @Test
        @DisplayName("Sollte alle Kunden für User zurückgeben")
        void testGetAllAsUser() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_USER);
            final var response = executeCustomersGraphQLQuery(customersQuery, null, client);
            assertValidCustomerResponse(response.getData());
        }

        @Test
        @DisplayName("Sollte Zugriff für Supreme verweigern")
        void testGetAllAsSupreme() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_SUPREME);
            final var response = executeCustomersGraphQLQuery(customersQuery, null, client);
            assertResponseStatus(response.getErrors().getFirst(), "FORBIDDEN");

        }

        @Test
        @DisplayName("Sollte Zugriff für Elite verweigern")
        void testGetAllAsElite() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ELITE);
            final var response = executeCustomersGraphQLQuery(customersQuery, null, client);
            assertResponseStatus(response.getErrors().getFirst(), "FORBIDDEN");
        }

        @Test
        @DisplayName("Sollte Zugriff für Basic verweigern")
        void testGetAllAsBasic() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_BASIC);
            final var response = executeCustomersGraphQLQuery(customersQuery, null, client);
            assertResponseStatus(response.getErrors().getFirst(), "FORBIDDEN");
        }

        @Test
        @DisplayName("Sollte Zugriff für Besucher verweigern")
        void testGetAllAsVisitor() {
            final var client = testClientProvider.getVisitorClient();
            final var response = executeCustomersGraphQLQuery(customersQuery, null, client);
            assertResponseStatus(response.getErrors().getFirst(), "UNAUTHORIZED");
        }
    }

    @DisplayName("Filter Kunden nach Geschlecht")
    @ParameterizedTest(name = "Sollte {1} Kunden für Geschlecht {0} zurückgeben")
    @CsvSource({
        GENDER_MALE + ", 13",
        GENDER_FEMALE + ", 11",
        GENDER_DIVERSE + ", 3"
    })
    void testGetAllFilterGender(String gender, int expectedSize) {
        final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
        final Map<String, Object> variables = Map.of(
            "field",  GENDER,
            "operator",EQ,
            "value", gender
        );
        final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

        assertAll(
            () -> assertThat(response.getData()).isNotNull(),
            () -> assertThat(response.getData())
                .isNotNull()
                .isNotEmpty()
                .hasSize(expectedSize),
            () -> assertThat(response.getData())
                .allMatch(customer -> customer.getGender().toString().equals(gender))
        );
    }

    @Nested
    @DisplayName("Tests für Benutzernamen-Filter")
    class UsernameFilterTests {

        @ParameterizedTest(name = "Sollte einen Kunden für exakten Benutzernamen {0} zurückgeben")
        @CsvSource({
            USERNAME_LEROY + ", " + USERNAME_LEROY + ", 1",
            USERNAME_CALEB + ", " + USERNAME_CALEB + ", 1",
            USERNAME_ERIK + "," + USERNAME_ERIK + ", 1",
        })
        void testGetAllFilterUsername(String searchUsername, String expectedUsername, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  USERNAME,
                "operator",EQ,
                "value", searchUsername
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData().getFirst().getUsername())
                    .isEqualTo(expectedUsername)
            );
        }

        @ParameterizedTest(name = "Sollte Kunden für Teilbenutzernamen {0} zurückgeben")
        @CsvSource({
            QUERY_SON + ", 3",
            QUERY_IVA + ", 2"
        })
        void testGetAllFilterPartialUsername(String partialUsername, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  USERNAME,
                "operator",LIKE,
                "value", partialUsername
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getUsername().toLowerCase().contains(partialUsername.toLowerCase())),
                () -> assertThat(response.getData().getFirst().getUsername().toLowerCase())
                    .contains(partialUsername.toLowerCase())
            );
        }
    }

    @Nested
    @DisplayName("Tests für Präfix-Filter")
    class PrefixFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für Präfix {0} zurückgeben")
        @CsvSource({
            QUERY_IVA + ", 2",
            QUERY_G + ", 4",
            QUERY_M + ", 2"
        })
        void testGetAllFilterPrefix(String prefix, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field", LAST_NAME,
                "operator",PREFIX,
                "value", prefix
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getLastName().toLowerCase().startsWith(prefix.toLowerCase())),
                () -> assertThat(response.getData().getFirst().getLastName().toLowerCase())
                    .startsWith(prefix.toLowerCase())
            );

            // Optional: Überprüfen Sie auch den letzten Benutzer
            if (expectedSize > 1) {
                assertThat(response.getData().get(expectedSize - 1).getLastName().toLowerCase())
                    .startsWith(prefix.toLowerCase());
            }
        }

        @Test
        @DisplayName("Sollte leere Liste für nicht existierenden Präfix zurückgeben")
        void testGetAllFilterNonExistingPrefix() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  LAST_NAME,
                "operator",LIKE,
                "value", QUERY_XYZ
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests für Nachnamen-Filter")
    class LastNameFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für Nachnamen-Filter {0} zurückgeben")
        @CsvSource({
            QUERY_M + ", 11",
            QUERY_SON + ", 4"
        })
        void testGetAllFilterLastName(String lastNameFilter, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  LAST_NAME,
                "operator",LIKE,
                "value", lastNameFilter
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getLastName().toLowerCase().contains(lastNameFilter.toLowerCase()))
            );
        }

        @Test
        @DisplayName("Sollte spezifische Nachnamen für Filter 'M' enthalten")
        void testGetAllFilterLastName_M_SpecificNames() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  LAST_NAME,
                "operator",LIKE,
                "value", QUERY_M
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            List<String> expectedLastNames = Arrays.asList("Meyer", "Müller", "Mustermann");
            assertThat(response.getData())
                .extracting(Customer::getLastName)
                .anyMatch(expectedLastNames::contains);
        }

        @Test
        @DisplayName("Sollte leere Liste für nicht existierenden Nachnamen zurückgeben")
        void testGetAllFilterNonExistingLastName() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  LAST_NAME,
                "operator",LIKE,
                "value", QUERY_XYZ
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Tests für E-Mail-Filter")
    class EmailFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für E-Mail-Filter {0} zurückgeben")
        @CsvSource({
            EMAIL_CALEB + ", 1",
            QUERY_IVANOV + ", 2",
            QUERY_ICLOUD_COM + ", 2"
        })
        void testGetAllFilterEmail(String emailFilter, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  EMAIL,
                "operator",LIKE,
                "value", emailFilter
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getEmail().toLowerCase().contains(emailFilter.toLowerCase()))
            );
        }

        @Test
        @DisplayName("Sollte exakte E-Mail-Adresse zurückgeben")
        void testGetAllFilterExactEmail() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  EMAIL,
                "operator", EQ,
                "value", EMAIL_CALEB
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(1),
                () -> assertThat(response.getData().getFirst().getEmail().toLowerCase())
                    .isEqualTo(EMAIL_CALEB.toLowerCase())
            );
        }

        @Test
        @DisplayName("Sollte leere Liste für nicht existierende E-Mail zurückgeben")
        void testGetAllFilterNonExistingEmail() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  EMAIL,
                "operator", EQ,
                "value", "nonexistent@example.com"
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Tests für Abonnement-Filter")
    class SubscriptionFilterTests {

        @ParameterizedTest(name = "Sollte Kunden basierend auf Abonnement-Status ({0}) zurückgeben")
        @CsvSource({
            QUERY_IS_SUBSCRIBED + ", 23, true",
            QUERY_IS_NOT_SUBSCRIBED + ", 4, false"
        })
        void testGetAllFilterSubscription(String subscriptionStatus, int expectedSize, boolean expectedSubscriptionStatus) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  "subscribed",
                "operator",EQ,
                "value", subscriptionStatus
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.isSubscribed() == expectedSubscriptionStatus)
            );
        }
    }

    @Nested
    @DisplayName("Tests für Tier-Level-Filter")
    class TierLevelFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für Tier-Level {0} zurückgeben")
        @CsvSource({
            TIER_LEVEL_1 + ", 9",
            TIER_LEVEL_2 + ", 9",
            TIER_LEVEL_3 + ", 9"
        })
        void testGetAllFilterTierLevel(String tierLevel, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  TIER_LEVEL,
                "operator",EQ,
                "value", tierLevel
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getTierLevel() == Integer.parseInt(tierLevel))
            );
        }

        @Test
        @DisplayName("Sollte spezifische Tier 1 Kunden enthalten")
        void testGetAllFilterTier1SpecificCustomers() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  TIER_LEVEL,
                "operator",EQ,
                "value", String.valueOf(TIER_LEVEL_1)
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);
            System.out.println(response.getData().stream().toList());

            List<String> expectedTier1Customers = Arrays.asList("julia", "erik", "john.muller");
            assertThat(response.getData())
                .extracting(Customer::getUsername)
                .containsAnyElementsOf(expectedTier1Customers);
        }

        @Test
        @DisplayName("Sollte leere Liste für ungültigen Tier-Level zurückgeben")
        void testGetAllFilterInvalidTierLevel() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  TIER_LEVEL,
                "operator",EQ,
                "value", INVALID_TIER_LEVEL_4
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);
            assertAll(
                () -> assertThat(response.getData()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("Tests für Geburtsdatum-Filter")
    class BirthdateFilterTests {

        static Stream<Arguments> provideBirthdateFilters() {
            return Stream.of(
                Arguments.of(QUERY_BIRTH_DATE_BEFORE, 18, LTE),
                Arguments.of(QUERY_BIRTH_DATE_AFTER, 4, GTE),
                Arguments.of(QUERY_BIRTH_DATE_BETWEEN, 5, IN)
            );
        }
        @ParameterizedTest(name = "Sollte Kunden für Geburtsdatum-Filter {0} zurückgeben")
        @MethodSource("provideBirthdateFilters")
//        @CsvSource({
//            QUERY_BIRTH_DATE_BEFORE + ", 18, before",
//            QUERY_BIRTH_DATE_AFTER + ", 4, after",
//            QUERY_BIRTH_DATE_BETWEEN + ", 5, between"
//        })
        void testGetAllFilterBirthdate(String birthdateFilter, int expectedSize, Operator operator) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  BIRTHDATE,
                "operator",operator.name(),
                "value", birthdateFilter
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> matchesBirthdateFilter(customer, birthdateFilter, operator))
            );
        }

        @Test
        @DisplayName("Sollte spezifische Kunden für Geburtsdatum vor 1999-05-03 enthalten")
        void testGetAllFilterBirthdateBeforeSpecificCustomers() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  BIRTHDATE,
                "operator",EQ,
                "value", NEW_USER_BIRTH_DATE
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            List<String> expectedCustomers = Arrays.asList("admin", "gentlecg99", "leroy135");
            assertThat(response.getData())
                .extracting(Customer::getUsername)
                .containsAnyElementsOf(expectedCustomers);
        }
    }

    @Nested
    @DisplayName("Tests für Familienstand-Filter")
    class MaritalStatusFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für Familienstand {0} zurückgeben")
        @CsvSource({
            MARITAL_STATUS_SINGLE + ", 7",
            MARITAL_STATUS_MARRIED + ", 15",
            MARITAL_STATUS_DIVORCED + ", 3",
            MARITAL_STATUS_WIDOW + ", 2"
        })
        void testGetAllFilterMaritalStatus(String getMaritalStatus, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  MARITAL_STATUS,
                "operator",EQ,
                "value", getMaritalStatus
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getMaritalStatus().toString().equals(getMaritalStatus))
            );
        }
    }

    @Nested
    @DisplayName("Tests für Kundenstatus-Filter")
    class CustomerStatusFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für Kundenstatus {0} zurückgeben")
        @CsvSource({
            CUSTOMER_STATUS_ACTIVE + ", 21",
            CUSTOMER_STATUS_BLOCKED + ", 2",
            CUSTOMER_STATUS_INACTIVE + ", 3",
            CUSTOMER_STATUS_CLOSED + ", 1"
        })
        void testGetAllFilterCustomerStatus(String customerStatus, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  CUSTOMER_STATUS,
                "operator",EQ,
                "value", customerStatus
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);
            
            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> customer.getCustomerState().toString().equals(customerStatus))
            );
        }
    }

    @Nested
    @DisplayName("Tests für Postleitzahl-Filter")
    class ZipCodeFilterTests {

        static Stream<Arguments> provideBirthdateFilters() {
            return Stream.of(
                Arguments.of(QUERY_ZIP_CODE_70374, 3, EQ),
                Arguments.of(QUERY_ZIP_CODE_Y1000, 1, LIKE),
                Arguments.of(QUERY_ZIP_CODE_KA, 2, PREFIX)
            );
        }
        @ParameterizedTest(name = "Sollte Kunden für Postleitzahl {0} zurückgeben")
        @MethodSource("provideBirthdateFilters")
//        @CsvSource({
//            QUERY_ZIP_CODE_70374 + ", 3, exact",
//            QUERY_ZIP_CODE_Y1000 + ", 1, partial",
//            QUERY_ZIP_CODE_KA + ", 2, prefix"
//        })
        void testGetAllFilterZipCode(String zipCode, int expectedSize, Operator operator) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  ZIP_CODE,
                "operator",operator.name(),
                "value", zipCode
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> matchesZipCode(customer.getAddress().getZipCode(), zipCode, operator))
            );
        }



        @Test
        @DisplayName("Sollte leere Liste für nicht existierende Postleitzahl zurückgeben")
        void testGetAllFilterNonExistingZipCode() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  ZIP_CODE,
                "operator",EQ,
                "value", "99999"
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Tests für Stadt-Filter")
    class CityFilterTests {

        static Stream<Arguments> provideBirthdateFilters() {
            return Stream.of(
                Arguments.of(QUERY_CITY_STUTTGART, 3, EQ),
                Arguments.of(QUERY_CITY_TOK, 1, LIKE)
            );
        }
        @MethodSource("provideBirthdateFilters")
        @ParameterizedTest(name = "Sollte Kunden für Stadt {0} zurückgeben")
//        @CsvSource({
//            QUERY_CITY_STUTTGART + ", 3, exact",
//            QUERY_CITY_TOK + ", 1, partial"
//        })
        void testGetAllFilterCity(String city, int expectedSize, Operator operator) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  CITY,
                "operator",operator.name(),
                "value", city
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> matchesCity(customer.getAddress().getCity(), city, operator))
            );
        }



        @Test
        @DisplayName("Sollte leere Liste für nicht existierende Stadt zurückgeben")
        void testGetAllFilterNonExistingCity() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  CITY,
                "operator",EQ,
                "value","NonExistentCity"
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Tests für Bundesland-Filter")
    class StateFilterTests {

        static Stream<Arguments> provideBirthdateFilters() {
            return Stream.of(
                Arguments.of(QUERY_STATE_NEW_SOUTH_WALES, 2, EQ),
                Arguments.of(QUERY_STATE_BA, 5, LIKE)
            );
        }
        @MethodSource("provideBirthdateFilters")
        @ParameterizedTest(name = "Sollte Kunden für Bundesland {0} zurückgeben")
//        @CsvSource({
//            QUERY_STATE_NEW_SOUTH_WALES + ", 1, exact",
//            QUERY_STATE_BA + ", 5, partial"
//        })
        void testGetAllFilterState(String state, int expectedSize, Operator operator) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  STATE,
                "operator",operator.name(),
                "value", state
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> matchesState(customer.getAddress().getState(), state, operator))
            );
        }

        @Test
        @DisplayName("Sollte leere Liste für nicht existierendes Bundesland zurückgeben")
        void testGetAllFilterNonExistingState() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  STATE,
                "operator",EQ,
                "value", "NonExistentState"
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Tests für Länder-Filter")
    class CountryFilterTests {

        static Stream<Arguments> provideBirthdateFilters() {
            return Stream.of(
                Arguments.of(QUERY_COUNTRY_USA, 2, EQ),
                Arguments.of(QUERY_COUNTRY_LAND, 11, LIKE)
            );
        }
        @MethodSource("provideBirthdateFilters")
        @ParameterizedTest(name = "Sollte Kunden für Land {0} zurückgeben")
//        @CsvSource({
//            QUERY_COUNTRY_USA + ", 3, exact",
//            QUERY_COUNTRY_LAND + ", 11, partial"
//        })
        void testGetAllFilterCountry(String country, int expectedSize, Operator operator) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  COUNTRY,
                "operator",operator.name(),
                "value", country
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> matchesCountry(customer.getAddress().getCountry(), country, operator))
            );
        }

        @Test
        @DisplayName("Sollte leere Liste für nicht existierendes Land zurückgeben")
        void testGetAllFilterNonExistingCountry() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  COUNTRY,
                "operator",EQ,
                "value", "NonExistentState"
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Tests für Kontaktoptionen-Filter")
    class ContactOptionsFilterTests {

        @ParameterizedTest(name = "Sollte Kunden für Kontaktoption {0} zurückgeben")
        @CsvSource({
            CONTACT_OPTION_PHONE + ", 20",
            CONTACT_OPTION_EMAIL + ", 22",
            CONTACT_OPTION_LETTER + ", 14",
            CONTACT_OPTION_SMS + ", 8"
        })
        void testGetAllFilterSingleContactOption(String contactOption, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  CONTACT_OPTIONS,
                "operator",EQ,
                "value", contactOption
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> hasContactOption(customer, contactOption))
            );
        }

        @ParameterizedTest(name = "Sollte Kunden für mehrere Kontaktoptionen ({0}) zurückgeben")
        @CsvSource({
            CONTACT_OPTION_PHONE + ";" + CONTACT_OPTION_EMAIL + ";" + CONTACT_OPTION_LETTER + ";" + CONTACT_OPTION_SMS + ", 5",
            CONTACT_OPTION_PHONE + ";" + CONTACT_OPTION_EMAIL + ", 17"
        })
        void testGetAllFilterMultipleContactOptions(String contactOptionsString, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Spaltet die Interessen anhand des Trennzeichens ";"
            List<String> contactOptions = Arrays.asList(contactOptionsString.split(";"));

            // ✅ Erzeugt eine Liste mit separaten Bedingungen für jedes Interesse
            List<Map<String, Object>> interestFilters = new ArrayList<>();
            for (String contactOption : contactOptions) {
                interestFilters.add(Map.of("field", CONTACT_OPTIONS, "operator", EQ, "value", contactOption));
            }

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", interestFilters
            );

            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> hasAnyContactOption(customer, contactOptions))
            );
        }

        private boolean hasContactOption(Customer customer, String contactOption) {
            return customer.getContactOptions().stream()
                .anyMatch(option -> option.name().equalsIgnoreCase(contactOption));
        }

        private boolean hasAnyContactOption(Customer customer, List<String> contactOptions) {
            return customer.getContactOptions().stream()
                .anyMatch(option -> contactOptions.contains(option.name().toUpperCase()));
        }
    }

    @Nested
    @DisplayName("Tests für Interessen-Filter")
    class InterestsFilterTests {

        @ParameterizedTest
        @DisplayName("Sollte Kunden für Interesse {0} zurückgeben")
        @CsvSource({
            INTEREST_INVESTMENTS + ", 6",
            INTEREST_TECHNOLOGY_AND_INNOVATION + ", 16"
        })
        void testGetAllFilterSingleInterest(String interest, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);
            final Map<String, Object> variables = Map.of(
                "field",  INTERESTS,
                "operator",EQ,
                "value", interest
            );
            final var response = executeCustomersGraphQLQuery(customersFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> hasInterest(customer, interest))
            );
        }

        @ParameterizedTest(name = "Sollte Kunden für mehrere Interessen ({0}) zurückgeben")
        @CsvSource({
            INTEREST_INVESTMENTS + ";" + INTEREST_SAVINGS_AND_FINANCES + ";" + INTEREST_CREDIT_AND_DEBT + ";" +
                INTEREST_BANK_PRODUCTS_AND_SERVICES + ";" + INTEREST_FINANCIAL_EDUCATION_AND_COUNSELING + ";" +
                INTEREST_REAL_ESTATE + ";" + INTEREST_INSURANCE + ";" + INTEREST_SUSTAINABLE_FINANCE + ";" +
                INTEREST_TECHNOLOGY_AND_INNOVATION + ";" + INTEREST_TRAVEL + ", 1",
            INTEREST_INVESTMENTS + ";" + INTEREST_REAL_ESTATE + ", 4"
        })
        void testGetAllFilterMultipleInterests(String interestsString, int expectedSize) {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Spaltet die Interessen anhand des Trennzeichens ";"
            List<String> interests = Arrays.asList(interestsString.split(";"));

            // ✅ Erzeugt eine Liste mit separaten Bedingungen für jedes Interesse
            List<Map<String, Object>> interestFilters = new ArrayList<>();
            for (String interest : interests) {
                interestFilters.add(Map.of("field", INTERESTS, "operator", EQ, "value", interest));
            }

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", interestFilters
            );
            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(expectedSize),
                () -> assertThat(response.getData())
                    .allMatch(customer -> hasAnyInterest(customer, interests))
            );
        }

        private boolean hasInterest(Customer customer, String interest) {
            return customer.getInterests().stream()
                .anyMatch(i -> i.name().equalsIgnoreCase(interest));
        }

        private boolean hasAnyInterest(Customer customer, List<String> interests) {
            return customer.getInterests().stream()
                .anyMatch(i -> interests.contains(i.name().toUpperCase()));
        }
    }

    @Nested
    @DisplayName("Tests für kombinierte Filter")
    class CombinedFilterTests {

        @Test
        @DisplayName("Sollte Kunden für Nachname und E-Mail-Filter zurückgeben")
        void testFilterLastNameAndEmail() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", List.of(
                    Map.of("field",LAST_NAME,"operator",LIKE,"value",QUERY_SON),
                    Map.of("field", EMAIL, "operator", LIKE, "value", QUERY_ICLOUD_COM)
                )
            );
            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(1),
                () -> assertThat(response.getData())
                    .allMatch(customer ->
                        customer.getLastName().toLowerCase().contains(QUERY_SON.toLowerCase())
                            && customer.getEmail().toLowerCase().endsWith(QUERY_ICLOUD_COM.toLowerCase())
                    )
            );
        }

        @Test
        @DisplayName("Sollte Kunden für Abonnement, Geschlecht, Familienstand und Kundenstatus zurückgeben")
        void testFilterSubscribedAndGenderAndMaritalStatusAndCustomerStatus() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", List.of(
                    Map.of("field","subscribed","operator",EQ,"value",QUERY_IS_SUBSCRIBED),
                    Map.of("field", GENDER, "operator", EQ, "value", GENDER_FEMALE),
                    Map.of("field",MARITAL_STATUS,"operator",EQ,"value",MARITAL_STATUS_MARRIED),
                    Map.of("field", CUSTOMER_STATUS, "operator", EQ, "value", CUSTOMER_STATUS_ACTIVE)
                )
            );
            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(7),
                () -> assertThat(response.getData())
                    .allMatch(customer ->
                        customer.isSubscribed() && customer.getGender().name().equals(GENDER_FEMALE) && customer.getMaritalStatus().name().equals(MARITAL_STATUS_MARRIED) && customer.getCustomerState().name().equals(CUSTOMER_STATUS_ACTIVE)
                    )
            );
        }

        @Test
        @DisplayName("Sollte Kunden für Geburtsdatum und Bundesland zurückgeben")
        void testFilterBirthdateAndState() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", List.of(
                    Map.of("field",BIRTHDATE,"operator",GTE,"value",QUERY_BIRTH_DATE_AFTER),
                    Map.of("field", STATE, "operator", PREFIX, "value", QUERY_STATE_BA)
                )
            );
            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);
            LocalDate cutoffDate = LocalDate.parse(QUERY_BIRTH_DATE_AFTER.split(";")[0]);

            assertAll(

                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(2),
                () -> assertThat(response.getData())
                    .allMatch(customer ->
                        customer.getBirthdate().isAfter(cutoffDate)
                            && customer.getAddress().getState().toLowerCase().contains(QUERY_STATE_BA.toLowerCase())
                    )
            );
        }

        @Test
        @DisplayName("Sollte Kunden für Land, Kontaktoption, Interesse und Kundenstufe zurückgeben")
        void testFilterCountryAndContactAndInterestAndTier() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", List.of(
                    Map.of("field",COUNTRY,"operator",LIKE,"value",QUERY_COUNTRY_LAND),
                    Map.of("field", CONTACT_OPTIONS, "operator", EQ, "value", CONTACT_OPTION_EMAIL),
                    Map.of("field",INTERESTS,"operator",EQ,"value",INTEREST_TECHNOLOGY_AND_INNOVATION),
                    Map.of("field", TIER_LEVEL, "operator", EQ, "value", String.valueOf(TIER_LEVEL_3))
                )
            );
            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isNotNull(),
                () -> assertThat(response.getData())
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(2),
                () -> assertThat(response.getData())
                    .allMatch(customer ->
                        customer.getAddress().getCountry().toLowerCase().contains(QUERY_COUNTRY_LAND.toLowerCase())
                            && customer.getContactOptions().stream().anyMatch(contact -> contact.name().equals(CONTACT_OPTION_EMAIL))
                            && customer.getInterests().stream().anyMatch(interest -> interest.name().equals(INTEREST_TECHNOLOGY_AND_INNOVATION))
                            && customer.getTierLevel() == Integer.parseInt(String.valueOf(TIER_LEVEL_3))
                    )
            );
        }

        @Test
        @DisplayName("Sollte NOT_FOUND zurückgeben für Benutzername, Kundenstufe und Interesse Filter ohne Ergebnisse")
        void testFilterUsernameAndTierAndInterest() {
            final var client = testClientProvider.getAuthenticatedClient(ROLE_ADMIN);

            // ✅ Setzt die Bedingungen in das Filter-Query
            final Map<String, Object> variables = Map.of(
                "and", List.of(
                    Map.of("field", USERNAME,"operator",LIKE,"value",QUERY_IVA),
                    Map.of("field", TIER_LEVEL, "operator", EQ, "value", String.valueOf(TIER_LEVEL_2)),
                    Map.of("field",INTERESTS,"operator",EQ,"value",INTEREST_INVESTMENTS)
                )
            );
            final var response = executeCustomersGraphQLQuery(customersMultipleFilterQuery, variables, client);

            assertAll(
                () -> assertThat(response.getData()).isEmpty()
            );
        }
    }

    private boolean matchesCountry(String actualCountry, String expectedCountry, Operator operator) {
        return switch (operator) {
            case EQ -> actualCountry.equalsIgnoreCase(expectedCountry);
            case LIKE -> actualCountry.toLowerCase().contains(expectedCountry.toLowerCase());
            default -> false;
        };
    }

    private boolean matchesState(String actualZipCode, String expectedZipCode, Operator operator) {
        return switch (operator) {
            case EQ -> actualZipCode.equalsIgnoreCase(expectedZipCode);
            case PREFIX ,LIKE -> actualZipCode.toLowerCase().contains(expectedZipCode.toLowerCase());
            default -> false;
        };
    }

    private boolean matchesCity(String actualZipCode, String expectedZipCode, Operator operator) {
        return switch (operator) {
            case EQ -> actualZipCode.equalsIgnoreCase(expectedZipCode);
            case PREFIX ,LIKE -> actualZipCode.toLowerCase().contains(expectedZipCode.toLowerCase());
            default -> false;
        };
    }

    private boolean matchesZipCode(String actualZipCode, String expectedZipCode, Operator operator) {
        return switch (operator) {
            case EQ -> actualZipCode.equalsIgnoreCase(expectedZipCode);
            case PREFIX ,LIKE -> actualZipCode.toLowerCase().contains(expectedZipCode.toLowerCase());
            default -> false;
        };
    }

    private boolean matchesBirthdateFilter(Customer customer, String filter, Operator operator) throws Exception {
        LocalDate customerBirthdate = customer.getBirthdate();

        // Falls das Datum nicht gesetzt ist, Rückgabe false
        if (customerBirthdate == null || filter == null || filter.isBlank()) {
            return false;
        }

        // Falls der Filter für IN genutzt wird, müssen es zwei Daten sein
        String[] parts = filter.split(",");
        LocalDate date1 = parseDate(parts[0]); // Erstes Datum
        LocalDate date2 = (parts.length > 1) ? parseDate(parts[1]) : null; // Zweites Datum, falls vorhanden

        if (date1 != null) {
            return switch (operator) {
                case LTE -> customerBirthdate.isBefore(date1) || customerBirthdate.isEqual(date1);
                case GTE -> customerBirthdate.isAfter(date1) || customerBirthdate.isEqual(date1);
                case EQ -> customerBirthdate.isEqual(date1);
                case IN -> {
                    if (date2 != null) {
                        yield (customerBirthdate.isEqual(date1) || customerBirthdate.isAfter(date1)) &&
                            (customerBirthdate.isEqual(date2) || customerBirthdate.isBefore(date2));
                    }
                    yield false;
                }
                default -> false;
            };
        }
        throw new Exception("data1 ist null");
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.err.println("Ungültiges Datumsformat: " + date);
            return null;
        }
    }

    private void assertValidCustomerResponse(List<Customer> customer) {
        assertAll(
            () -> assertThat(customer).isNotNull(),
            () -> assertThat(customer)
                .isNotNull()
                .isNotEmpty()
                .hasSize(TOTAL_CUSTOMERS),
            () -> assertThat(customer)
                .extracting(Customer::getUsername)
                .contains("admin", "rae", "erik")
        );
    }

    private void assertResponseStatus(final CustomGraphQLError error, final String expectedStatus) {
        assertThat(error.getExtensions().get("classification")).isEqualTo(expectedStatus);
    }

}
package com.gentlecorp.customer.test;

import com.gentlecorp.customer.Env;
import com.gentlecorp.customer.config.TestClientProvider;
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

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCreateCustomer extends CustomerCommonFunctions {
    private static final Logger log = LoggerFactory.getLogger(TestCreateCustomer.class);

    @BeforeAll
    protected void setup() {
        new Env();
        testClientProvider = new TestClientProvider();
        testClientProvider.init(serverPort);
    }

    @Nested
    @DisplayName("Tests für das erstellen von Kunden")
    class CreateTests {

        @ParameterizedTest(name = "Erstelle einen Kunden mit dem {2}. Rang, der Email {1} und dem benutzernamen {0} ")
        @CsvSource({
            SUPREME_USERNAME + ", " + SUPREME_EMAIL + ", " + TIER_LEVEL_3,
            ELITE_USERNAME + ", " + ELITE_EMAIL + ", " + TIER_LEVEL_2,
            BASIC_USERNAME + ", " + BASIC_EMAIL + ", " + TIER_LEVEL_1
        })
        void testCreateCustomer(final String username, final String email, final int tierLevel) {
            // 1. Erstellen Sie den Kunden
            final var newCustomerId = createCustomer(username, email, tierLevel);

            // 2. Überprüfen Sie den erstellten Kunden als Admin
            verifyCustomerAsAdmin(newCustomerId, username, email, tierLevel);

            // 3. Überprüfen Sie die Zugriffsrechte
            verifyAccessRights(newCustomerId);

            // 4. Erstellen Sie einen Basic-Kunden mit denselben Daten
            createAndVerifyCustomer(newCustomerId, username, email, tierLevel);

            // 5. Löschen Sie den Kunden und überprüfen Sie die Löschung
            deleteAndVerifyCustomer(newCustomerId,1);
        }


        @ParameterizedTest(name = "Fehlgeschlagene Kundenerstellung mit ungültigem Wert für {0}")
        @CsvSource({
            LAST_NAME + ", " + INVALID_LAST_NAME + ",Der Nachname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.",
            FIRST_NAME + ", " + INVALID_FIRST_NAME + ",Der Vorname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.",
            EMAIL + ", " + INVALID_EMAIL  + ",Bitte gib eine gültige E-Mail-Adresse an.",
            USERNAME + ", " + INVALID_USERNAME + ",Der Benutzername muss zwischen 4 und 20 Zeichen lang sein. Der Benutzername darf nur Buchstaben; Zahlen; Unterstriche; Punkte oder Bindestriche enthalten.",
            PHONE_NUMBER + ", " + INVALID_PHONE_NUMBER + ",Bitte gib eine gültige Telefonnummer an. Die Telefonnummer muss zwischen 7 und 25 Zeichen lang sein.",
        })
        void testFailCreateCustomerWithInvalidStringInput(
            final String attribut, final String invalidInput, final String message) {
            final Map<String, Object> input = createBaseCustomerInput();
            input.put(attribut, invalidInput);
            final Map<String, Object> variables = Map.of("input", input, PASSWORD, NEW_USER_PASSWORD);
            validateErrorResponse(variables, message.replace(";", ","));
        }

            @Test
            @DisplayName("Fehlgeschlagene Kundenerstellung mit ungültigem Wert für Geburtstag 01.01.2023")
            void testFailCreateCustomerWithInvalidBirthday() {
            final Map<String, Object> input = createBaseCustomerInput();
            input.put(BIRTHDATE, FUTURE_BIRTHDATE);
            final Map<String, Object> variables = Map.of("input", input, PASSWORD, NEW_USER_PASSWORD);
            validateErrorResponse(variables, "Das Geburtsdatum muss in der Vergangenheit liegen.");
        }

        @ParameterizedTest(name = "Fehlgeschlagene Kundenerstellung mit ungültigem Wert für Tier level {0}")
        @CsvSource({
            "0,Die Mitgliedschaftsstufe muss mindestens 1 sein.",
            INVALID_TIER_LEVEL_4 + ",Die Mitgliedschaftsstufe darf maximal 3 sein."
        })
        void testFailCreateCustomerWithInvalidTier(final int invalidTierLevel, final String message) {
            final Map<String, Object> input = createBaseCustomerInput();
            input.put(TIER_LEVEL, invalidTierLevel);
            final Map<String, Object> variables = Map.of("input", input, PASSWORD, NEW_USER_PASSWORD);
            validateErrorResponse(variables, message);
        }


        static Stream<Arguments> provideMissingAttributes() {
            return Stream.of(
                Arguments.of(LAST_NAME, "Bitte gib deinen Nachnamen an."),
                Arguments.of(FIRST_NAME, "Bitte gib deinen Vornamen an."),
                Arguments.of(EMAIL, "Bitte gib deine E-Mail-Adresse an."),
                Arguments.of(USERNAME, "Bitte gib einen Benutzernamen an."),
                //Arguments.of(PHONE_NUMBER, "Die Telefonnummer ist erforderlich."),
                Arguments.of(BIRTHDATE, "Das Geburtsdatum ist erforderlich."),
                Arguments.of(TIER_LEVEL, "Die Mitgliedschaftsstufe muss mindestens 1 sein."),
                Arguments.of(GENDER, "Bitte gib dein Geschlecht an."),
                Arguments.of(MARITAL_STATUS, "Bitte gib deinen Familienstand an."),
                //Arguments.of(ADDRESS, "Die Adresse ist erforderlich."),
                Arguments.of(CONTACT_OPTIONS, "Bitte gib mindestens eine bevorzugte Kontaktoption an.")
                //Arguments.of(INTERESTS, "Mindestens ein Interesse muss angegeben werden.")
            );
        }

        @ParameterizedTest(name = "Fehlgeschlagene Kundenerstellung: {0} fehlt")
        @MethodSource("provideMissingAttributes")
        @DisplayName("Fehlgeschlagene Kundenerstellung mit fehlendem Attribut")
        void testFailCreateCustomerWithoutAttribute(String missingAttribute, String expectedMessage) {
            final Map<String, Object> input = createBaseCustomerInput();
            input.remove(missingAttribute);

            final Map<String, Object> variables = Map.of("input", input, PASSWORD, NEW_USER_PASSWORD);

            validateErrorResponse(variables, expectedMessage);
        }

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
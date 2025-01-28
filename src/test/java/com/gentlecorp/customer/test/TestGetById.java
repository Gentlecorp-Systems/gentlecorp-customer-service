package com.gentlecorp.customer.test;


import com.gentlecorp.customer.model.TestCustomer;
import com.gentlecorp.customer.model.dto.AddressDTO;
import com.gentlecorp.customer.model.enums.*;
import com.gentlecorp.customer.utils.CustomerCommonFunctions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class TestGetById extends CustomerCommonFunctions {

    @Test
    void testGetAllCustomersByIds() {
        assertThat(testClientProvider).isNotNull();
        assertThat(testClientProvider.adminClient).isNotNull();

        for (int i = 0; i <= 26; i++) {
            String customerId = String.format("00000000-0000-0000-0000-%012d", i); // Erstelle UUIDs im Format 00000000-0000-0000-0000-000000000000 bis 00000000-0000-0000-0000-000000000026
            String url = SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId;

            ResponseEntity<TestCustomer> response = testClientProvider.adminClient.getForEntity(url, TestCustomer.class);

            // Überprüfe, ob die Antwort erfolgreich ist und der Body nicht null ist
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            System.out.println("Customer ID: " + customerId + " successfully retrieved.");
        }
    }


//    @Test
//    void testGetHiroshiByIdAsAdmin() {
//        assertThat(testClientProvider).isNotNull();
//        assertThat(testClientProvider.adminClient).isNotNull();
//        ResponseEntity<TestCustomer> response = testClientProvider.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyHiroshiDetails(response.getBody());
//    }
//
//    @Test
//    void testGetHiroshiByIdAsUser() {
//        var response = testClientProvider.userClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyHiroshiDetails(response.getBody());
//    }
//
//    @Test
//    void testGetHiroshiByIdAsSupreme() {
//        var response = testClientProvider.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void testGetHiroshiByIdAsElite() {
//        var response = testClientProvider.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void testGetHiroshiByIdAsBasic() {
//        var response = testClientProvider.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void testGetHiroshiByIdAsVisitor() {
//        var response = testClientProvider.visitorClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }
//
//    @Test
//    void testGetCustomerByIdNotFound() {
//        var response = testClientProvider.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + NOT_EXISTING_ID, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    void testGetErikByIdAsErik() {
//        var response = testClientProvider.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_ERIK, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyErikDetails(response.getBody());
//    }
//
//    @Test
//    void testGetLeroyByIdAsLeroy() {
//        var response = testClientProvider.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_LEROY, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyLeroyDetails(response.getBody());
//    }
//
//    @Test
//    void testGetCalebByIdAsCaleb() {
//        var response = testClientProvider.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ID_CALEB, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyCalebDetails(response.getBody());
//    }
//
//    @Test
//    void testGetFullHiroshiByIdAsAdmin() {
//        ResponseEntity<TestCustomer> response = testClientProvider.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ALL_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyFullHiroshiDetails(response.getBody());
//    }
//
//    @Test
//    void testGetFullHiroshiByIdAsUser() {
//        ResponseEntity<TestCustomer> response = testClientProvider.userClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ALL_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        verifyFullHiroshiDetails(response.getBody());
//    }
//
//    @Test
//    void testGetFullHiroshiByIdAsSupreme() {
//        ResponseEntity<TestCustomer> response = testClientProvider.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ALL_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void testGetFullHiroshiByIdAsElite() {
//        ResponseEntity<TestCustomer> response = testClientProvider.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ALL_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void testGetFullHiroshiByIdAsBasic() {
//        ResponseEntity<TestCustomer> response = testClientProvider.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + ALL_PATH + ID_HIROSHI, TestCustomer.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void testGetHiroshiByIdNotModified() {
//        String url = SCHEMA_HOST + port + CUSTOMER_PATH + ID_HIROSHI;
//        var headers = createHeaders(HEADER_IF_NONE_MATCH, ETAG_VALUE_0);
//
//        ResponseEntity<TestCustomer> response = testClientProvider.adminClient.exchange(
//            url,
//            HttpMethod.GET,
//            new HttpEntity<>(headers),
//            TestCustomer.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
//    }

    private void verifyHiroshiDetails(TestCustomer customer) {
        assertThat(customer.username()).isEqualTo(USERNAME_HIROSHI);
        assertThat(customer.lastName()).isEqualTo(LAST_NAME_HIROSHI);
        assertThat(customer.firstName()).isEqualTo(FIRST_NAME_HIROSHI);
        assertThat(customer.email()).isEqualTo(EMAIL_HIROSHI);
        assertThat(customer.phoneNumber()).isEqualTo(PHONE_NUMBER_HIROSHI);
        assertThat(customer.subscribed()).isTrue();
        assertThat(customer.tierLevel()).isEqualTo(TIER_LEVEL_1);
        assertThat(customer.birthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_HIROSHI));
        assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
        assertThat(customer.gender()).isEqualTo(GenderType.MALE);
        assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

        verifyLinks(customer._links(), ID_HIROSHI);
    }

    private void verifyLeroyDetails(TestCustomer customer) {
        assertThat(customer.username()).isEqualTo(USERNAME_LEROY);
        assertThat(customer.lastName()).isEqualTo(LAST_NAME_LEROY);
        assertThat(customer.firstName()).isEqualTo(FIRST_NAME_LEROY);
        assertThat(customer.email()).isEqualTo(EMAIL_LEROY);
        assertThat(customer.phoneNumber()).isEqualTo(PHONE_NUMBER_LEROY);
        assertThat(customer.subscribed()).isTrue();
        assertThat(customer.tierLevel()).isEqualTo(TIER_LEVEL_2);
        assertThat(customer.birthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_LEROY));
        assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
        assertThat(customer.gender()).isEqualTo(GenderType.MALE);
        assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.SINGLE);

        verifyLinks(customer._links(), ID_LEROY);
    }

    private void verifyErikDetails(TestCustomer customer) {
        assertThat(customer.username()).isEqualTo(USERNAME_ERIK);
        assertThat(customer.lastName()).isEqualTo(LAST_NAME_ERIK);
        assertThat(customer.firstName()).isEqualTo(FIRST_NAME_ERIK);
        assertThat(customer.email()).isEqualTo(EMAIL_ERIK);
        assertThat(customer.phoneNumber()).isEqualTo(PHONE_NUMBER_ERIK);
        assertThat(customer.subscribed()).isFalse();
        assertThat(customer.tierLevel()).isEqualTo(TIER_LEVEL_1);
        assertThat(customer.birthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_ERIK));
        assertThat(customer.customerState()).isEqualTo(StatusType.INACTIVE);
        assertThat(customer.gender()).isEqualTo(GenderType.MALE);
        assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

        verifyLinks(customer._links(), ID_ERIK);
    }

    private void verifyCalebDetails(TestCustomer customer) {
        assertThat(customer.username()).isEqualTo(USERNAME_CALEB);
        assertThat(customer.lastName()).isEqualTo(LAST_NAME_CALEB);
        assertThat(customer.firstName()).isEqualTo(FIRST_NAME_CALEB);
        assertThat(customer.email()).isEqualTo(EMAIL_CALEB);
        assertThat(customer.phoneNumber()).isEqualTo(PHONE_NUMBER_CALEB);
        assertThat(customer.subscribed()).isTrue();
        assertThat(customer.tierLevel()).isEqualTo(TIER_LEVEL_3);
        assertThat(customer.birthdate()).isEqualTo(LocalDate.parse(BIRTH_DATE_CALEB));
        assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
        assertThat(customer.gender()).isEqualTo(GenderType.MALE);
        assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

        verifyLinks(customer._links(), ID_CALEB);
    }

    private void verifyFullHiroshiDetails(TestCustomer customer) {
        verifyHiroshiDetails(customer);
        verifyHiroshiAddress(customer.address());
        verifyHiroshiInterestsAndContactOptions(customer);
        //verifyHiroshiContacts(customer.contacts());
    }

    private void verifyHiroshiInterestsAndContactOptions(TestCustomer customer) {
        assertThat(customer.interests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
        assertThat(customer.contactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.PHONE);
    }

    private void verifyHiroshiAddress(AddressDTO address) {
        assertThat(address.street()).isEqualTo(STREET_HIROSHI);
        assertThat(address.houseNumber()).isEqualTo(HOUSE_NUMBER_HIROSHI);
        assertThat(address.zipCode()).isEqualTo(ZIP_CODE_HIROSHI);
        assertThat(address.city()).isEqualTo(CITY_HIROSHI);
        assertThat(address.state()).isEqualTo(STATE_HIROSHI);
        assertThat(address.country()).isEqualTo(COUNTRY_HIROSHI);
    }

}

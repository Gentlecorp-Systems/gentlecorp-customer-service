package com.gentlecorp.customer.utils;

import com.gentlecorp.customer.config.TestClientProvider;
import com.gentlecorp.customer.model.HateoasLinks;
import com.gentlecorp.customer.model.TestCustomer;
import com.gentlecorp.customer.model.dto.AddressDTO;
import com.gentlecorp.customer.testData.CustomerTestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerCommonFunctions extends CustomerTestData {


  @Autowired
  protected TestClientProvider testClientProvider;

  @LocalServerPort
  public int port;

  String extractIdFromLocationHeader(URI locationUri) {
    String path = locationUri.getPath();
    return "/" + path.substring(path.lastIndexOf('/') + 1);
  }

  // Hilfsmethoden für die Erstellung des Request-Bodys und die Überprüfung von Details
  HttpEntity<Map<String, Object>> createRequestBody(final String username, final String email, final int tierLevel) {
    Map<String, Object> requestBody = new HashMap<>();

    Map<String, Object> customer = new HashMap<>();
    customer.put(LAST_NAME, NEW_USER_LAST_NAME);
    customer.put(FIRST_NAME, NEW_USER_FIRST_NAME);
    customer.put(EMAIL, email);
    customer.put(PHONE_NUMBER, NEW_USER_PHONE_NUMBER);
    customer.put(TIER_LEVEL, tierLevel);
    customer.put(IS_SUBSCRIBED, NEW_USER_SUBSCRIPTION);
    customer.put(BIRTHDATE, NEW_USER_BIRTH_DATE);
    customer.put(GENDER, NEW_USER_GENDER);
    customer.put(MARITAL_STATUS, NEW_USER_MARITAL_STATUS);
    customer.put(INTERESTS, List.of(NEW_USER_INTERESTS));
    customer.put(CONTACT_OPTIONS, List.of(NEW_USER_CONTACT_OPTIONS));
    customer.put(USERNAME, username);

    Map<String, Object> address = new HashMap<>();
    address.put(STREET, NEW_USER_STREET);
    address.put(HOUSE_NUMBER, NEW_USER_HOUSE_NUMBER);
    address.put(ZIP_CODE, NEW_USER_ZIP_CODE);
    address.put(STATE, NEW_USER_STATE);
    address.put(CITY, NEW_USER_CITY);
    address.put(COUNTRY, NEW_USER_COUNTRY);
    customer.put(ADDRESS, address);

    requestBody.put(CUSTOMER, customer);

    Map<String, Object> passwordMap = new HashMap<>();
    passwordMap.put(PASSWORD, NEW_USER_PASSWORD);
    requestBody.put(PASSWORD, passwordMap);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(requestBody, headers);
  }

  String createCustomer(final String username, final String email, final int tierLevel) {
    var request = createRequestBody(username, email, tierLevel);

    ResponseEntity<Void> response = testClientProvider.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNull();
    assertThat(response.getHeaders().getLocation()).isNotNull();
    assertThat(response.getHeaders().getLocation().toString())
      .startsWith(SCHEMA_HOST + port + CUSTOMER_PATH);

    return extractIdFromLocationHeader(response.getHeaders().getLocation());
  }

  void verifyCustomerAsAdmin(String customerId, String expectedUsername, final String expectedEmail, int expectedTierLevel) {
    var newCustomerClient = testClientProvider.createAuthenticatedClient(expectedUsername, NEW_USER_PASSWORD);

    var getResponse = newCustomerClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
      TestCustomer.class
    );

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getResponse.getBody()).isNotNull();

    var createdCustomer = getResponse.getBody();
    verifyCustomerDetails(createdCustomer, expectedUsername, expectedEmail, expectedTierLevel);
    verifyLinks(createdCustomer._links(), customerId);
  }

  void verifyCustomerAsCustomer(final String customerId, final String expectedUsername, final String expectedEmail, int expectedTierLevel) {
    ResponseEntity<TestCustomer> getResponse = testClientProvider.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      TestCustomer.class
    );
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getResponse.getBody()).isNotNull();

    var createdCustomer = getResponse.getBody();
    verifyCustomerDetails(createdCustomer, expectedUsername, expectedEmail,expectedTierLevel);
    verifyLinks(createdCustomer._links(), customerId);
  }

  void verifyAccessRights(String customerId) {
    assertThat(testClientProvider.supremeClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      TestCustomer.class
    ).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    assertThat(testClientProvider.eliteClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      TestCustomer.class
    ).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    assertThat(testClientProvider.basicClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      TestCustomer.class
    ).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  void createAndVerifyCustomer(String originalCustomerId, String username, final String expectedEmail,  int expectedTierLevel) {
    var newCustomerClient = testClientProvider.createAuthenticatedClient(username, NEW_USER_PASSWORD);

    ResponseEntity<TestCustomer> getResponse = newCustomerClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  originalCustomerId,
      TestCustomer.class
    );
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getResponse.getBody()).isNotNull();

    var createdCustomer = getResponse.getBody();
    verifyCustomerDetails(createdCustomer, username, expectedEmail, expectedTierLevel);

    verifyAddress(createdCustomer.address());
    verifyInterestsAndContactOptions(createdCustomer);
    verifyLinks(createdCustomer._links(), originalCustomerId);
  }

  void deleteAndVerifyCustomer(String customerId) {
    var requestEntity = createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0);
    ResponseEntity<Void> deleteResponse = testClientProvider.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      HttpMethod.DELETE,
      new HttpEntity<>(requestEntity),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<TestCustomer> getDeletedCustomerResponse = testClientProvider.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      TestCustomer.class
    );
    assertThat(getDeletedCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  void verifyAddress(AddressDTO address) {
    assertThat(address).satisfies(addr -> {
      assertThat(addr.street()).isEqualTo(NEW_USER_STREET);
      assertThat(addr.houseNumber()).isEqualTo(NEW_USER_HOUSE_NUMBER);
      assertThat(addr.zipCode()).isEqualTo(NEW_USER_ZIP_CODE);
      assertThat(addr.city()).isEqualTo(NEW_USER_CITY);
      assertThat(addr.state()).isEqualTo(NEW_USER_STATE);
      assertThat(addr.country()).isEqualTo(NEW_USER_COUNTRY);
    });
  }

  void verifyInterestsAndContactOptions(TestCustomer customer) {
    assertThat(customer.interests())
      .allMatch(interest -> interest.getInterest().equals(NEW_USER_INTERESTS));

    assertThat(customer.contactOptions())
      .allMatch(contactOption -> contactOption.getOption().equalsIgnoreCase(NEW_USER_CONTACT_OPTIONS));
  }

  protected void verifyLinks(HateoasLinks hateoaslinks, String customerId) {
    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + customerId;
    assertThat(hateoaslinks).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
      assertThat(links.list().href()).isEqualTo(baseUri);
      assertThat(links.add().href()).isEqualTo(baseUri);
      assertThat(links.update().href()).isEqualTo(idUri);
      assertThat(links.remove().href()).isEqualTo(idUri);
    });
  }

  void verifyCustomerDetails(final TestCustomer customer, final String expectedUsername, final String expectedEmail, int expectedTierLevel) {


    assertThat(customer.username()).isEqualTo(expectedUsername);
    assertThat(customer.lastName()).isEqualTo(NEW_USER_LAST_NAME);
    assertThat(customer.firstName()).isEqualTo(NEW_USER_FIRST_NAME);
    assertThat(customer.email()).isEqualTo(expectedEmail);
    assertThat(customer.phoneNumber()).isEqualTo(NEW_USER_PHONE_NUMBER);
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(expectedTierLevel);
    assertThat(customer.birthdate()).isEqualTo(LocalDate.parse(NEW_USER_BIRTH_DATE));
    assertThat(customer.gender().getGender().toLowerCase()).isEqualTo(NEW_USER_GENDER.toLowerCase());
    assertThat(customer.maritalStatus().getStatus().toLowerCase()).isEqualTo(NEW_USER_MARITAL_STATUS.toLowerCase());

    verifyAddress(customer.address());
    verifyInterestsAndContactOptions(customer);
  }

  void deleteAndVerifyCustomer2(String customerId) {
    var requestEntity = createHeaders(HEADER_IF_MATCH, ETAG_VALUE_1);
    ResponseEntity<Void> deleteResponse = testClientProvider.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      HttpMethod.DELETE,
      new HttpEntity<>(requestEntity),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<TestCustomer> getDeletedCustomerResponse = testClientProvider.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
      TestCustomer.class
    );
    assertThat(getDeletedCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  void deleteAndVerifyCustomer3(String customerId) {
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(testClientProvider.adminClient.getRestTemplate().getInterceptors());

    testClientProvider.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_2);
      return execution.execute(request, body);
    });

    try {
      ResponseEntity<Void> deleteResponse = testClientProvider.adminClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
        HttpMethod.DELETE,
        null,
        Void.class
      );
      assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

      ResponseEntity<TestCustomer> getDeletedCustomerResponse = testClientProvider.adminClient.getForEntity(
        SCHEMA_HOST + port + CUSTOMER_PATH +  customerId,
        TestCustomer.class
      );
      assertThat(getDeletedCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      testClientProvider.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }

  Map<String, Object> createContactBody() {
    Map<String, Object> contactBody = new HashMap<>();
    contactBody.put(LAST_NAME, NEW_CONTACT_LAST_NAME);
    contactBody.put(FIRST_NAME, NEW_CONTACT_FIRST_NAME);
    contactBody.put(RELATIONSHIP, NEW_CONTACT_RELATIONSHIP);
    contactBody.put(WITHDRAWAL_LIMIT, NEW_CONTACT_WITHDRAWAL_LIMIT);
    contactBody.put(IS_EMERGENCY_CONTACT, NEW_CONTACT_IS_EMERGENCY);
    return contactBody;
  }

  Map<String, Object> createExistingContactBody() {
    Map<String, Object> contactBody = new HashMap<>();
    contactBody.put(LAST_NAME, EXISTING_CONTACT_LAST_NAME);
    contactBody.put(FIRST_NAME, EXISTING_CONTACT_FIRST_NAME);
    contactBody.put(RELATIONSHIP, EXISTING_CONTACT_RELATIONSHIP);
    contactBody.put(WITHDRAWAL_LIMIT, EXISTING_CONTACT_WITHDRAWAL_LIMIT);
    contactBody.put(IS_EMERGENCY_CONTACT, EXISTING_CONTACT_IS_EMERGENCY);
    return contactBody;
  }

  Map<String, Object> createInvalidContactBody() {
    Map<String, Object> invalidContactBody = new HashMap<>();
    invalidContactBody.put(LAST_NAME, INVALID_CONTACT_LAST_NAME);
    invalidContactBody.put(FIRST_NAME, INVALID_CONTACT_FIRST_NAME);
    invalidContactBody.put(RELATIONSHIP, INVALID_CONTACT_RELATIONSHIP);
    invalidContactBody.put(WITHDRAWAL_LIMIT, INVALID_CONTACT_WITHDRAWAL_LIMIT);
    invalidContactBody.put(IS_EMERGENCY_CONTACT, INVALID_CONTACT_IS_EMERGENCY);
    return invalidContactBody;
  }

  protected HttpHeaders createHeaders(final String header, final String etagValue) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(header, etagValue);
    return headers;
  }

  Map<String, Object> createUpdatePassword(final String password) {
    Map<String, Object> updateRequest = new HashMap<>();
    updateRequest.put(PASSWORD, password);
    return updateRequest;
  }

  Map<String, Object> createUpdateRequestBody() {
    Map<String, Object> updateRequest = new HashMap<>();

    updateRequest.put(LAST_NAME, UPDATED_LAST_NAME);
    updateRequest.put(FIRST_NAME, UPDATED_FIRST_NAME);
    updateRequest.put(USERNAME, UPDATED_USERNAME);
    updateRequest.put(EMAIL, UPDATED_EMAIL);
    updateRequest.put(PHONE_NUMBER, UPDATED_PHONE_NUMBER);
    updateRequest.put(TIER_LEVEL, UPDATED_TIER_LEVEL);
    updateRequest.put(IS_SUBSCRIBED, UPDATED_IS_SUBSCRIBED);
    updateRequest.put(BIRTHDATE, UPDATED_BIRTH_DATE);
    updateRequest.put(GENDER, UPDATED_GENDER);
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

  Map<String, Object> createContactRequestBody() {
    Map<String, Object> updateRequest = new HashMap<>();

    updateRequest.put(LAST_NAME, UPDATED_LAST_NAME);
    updateRequest.put(FIRST_NAME, UPDATED_FIRST_NAME);
    updateRequest.put("relationship","PN");
    updateRequest.put("withdrawalLimit", 50);
    updateRequest.put("isEmergencyContact", true);

    return updateRequest;
  }

  void verifyUpdatedCustomer(TestCustomer customer) {
    assertThat(customer.lastName()).isEqualTo(UPDATED_LAST_NAME);
    assertThat(customer.firstName()).isEqualTo(UPDATED_FIRST_NAME);
    assertThat(customer.email()).isEqualTo(UPDATED_EMAIL);
    assertThat(customer.phoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    assertThat(customer.tierLevel()).isEqualTo(UPDATED_TIER_LEVEL);
    assertThat(customer.subscribed()).isEqualTo(UPDATED_IS_SUBSCRIBED);
    assertThat(customer.birthdate()).isEqualTo(LocalDate.parse(UPDATED_BIRTH_DATE));
    assertThat(customer.gender().getGender()).isEqualToIgnoringCase(UPDATED_GENDER);
    assertThat(customer.maritalStatus().getStatus()).isEqualToIgnoringCase(UPDATED_MARITAL_STATUS);
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

  Map<String, Object> createInvalidUpdateRequestBody() {
    Map<String, Object> updateRequest = createUpdateRequestBody();
    updateRequest.put(EMAIL, "invalid-email");
    updateRequest.put(PHONE_NUMBER, "123"); // Zu kurz
    updateRequest.put(TIER_LEVEL, 10); // Ungültiger Wert
    return updateRequest;
  }

  void assertForbidden(final ResponseEntity<ProblemDetail> problemDetail, final String role, final String customerId) {
    assertThat(problemDetail.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(problemDetail).isNotNull();
    assertThat(Objects.requireNonNull(problemDetail.getBody()).getDetail()).contains(String.format("Unzureichende RoleType als: %s", role));
    deleteAndVerifyCustomer(customerId);
  }

  void assertCreated(final ResponseEntity<Void> voidResponse, final String customerId) {
    assertThat(voidResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(voidResponse.getHeaders().getLocation()).isNotNull();

    deleteAndVerifyCustomer2(customerId);
  }

  TestRestTemplate getClient(final String role) {
    return switch (role) {
      case "ADMIN" -> testClientProvider.adminClient;
      case "BASIC" -> testClientProvider.basicClient;
      case "SUPREME" -> testClientProvider.supremeClient;
      case "ELITE" -> testClientProvider.eliteClient;
      case "USER" -> testClientProvider.userClient;
      default -> throw new IllegalArgumentException("Invalid role: " + role);
    };
  }
}

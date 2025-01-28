package com.gentlecorp.customer.config;

import com.gentlecorp.customer.testData.CustomerTestData;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TestClientProvider extends CustomerTestData {

  private final TestRestTemplate restTemplate;

  public TestClientProvider(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public TestRestTemplate adminClient;
  public TestRestTemplate userClient;
  public TestRestTemplate basicClient;
  public TestRestTemplate eliteClient;
  public TestRestTemplate supremeClient;
  public TestRestTemplate visitorClient;


  @PostConstruct
  public void init() {
    adminClient = createAuthenticatedClient(ROLE_ADMIN, ROLE_PASSWORD);
    userClient = createAuthenticatedClient(ROLE_USER, ROLE_PASSWORD);
    basicClient = createAuthenticatedClient(ROLE_BASIC, ROLE_PASSWORD);
    eliteClient = createAuthenticatedClient(ROLE_ELITE, ROLE_PASSWORD);
    supremeClient = createAuthenticatedClient(ROLE_SUPREME, ROLE_PASSWORD);
    visitorClient = createVisitorClient();
  }

  private TestRestTemplate createVisitorClient() {
    return new TestRestTemplate();
  }

  public TestRestTemplate createAuthenticatedClient(String username, String password) {
    ResponseEntity<Map> response = restTemplate.postForEntity(
      LOGIN_PATH,
      Map.of(USERNAME, username, PASSWORD, password),
      Map.class
    );

    assert response.getStatusCode().is2xxSuccessful();
    String token = (String) Objects.requireNonNull(response.getBody()).get(ACCESS_TOKEN);

    TestRestTemplate authenticatedClient = new TestRestTemplate();
    authenticatedClient.getRestTemplate().setInterceptors(
      List.of((request, body, execution) -> {
          request.getHeaders().add(AUTHORIZATION, BEARER + token);
        return execution.execute(request, body);
      })
    );
    return authenticatedClient;
  }

}

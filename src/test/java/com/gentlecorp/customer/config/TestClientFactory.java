package com.gentlecorp.customer.config;

import com.gentlecorp.customer.testData.CustomerTestData;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TestClientFactory extends CustomerTestData {

  private final TestRestTemplate restTemplate;

  public TestClientFactory(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private void setHeader(final TestRestTemplate client, final String token) {
    client.getRestTemplate().setInterceptors(
      List.of((request, body, execution) -> {
        if (token != null) {
          request.getHeaders().add(AUTHORIZATION, BEARER + token);
        }
        request.getHeaders().add(HEADER_IF_NONE_MATCH, ETAG_VALUE_MINUS_1);
        request.getHeaders().add(HEADER_IF_MATCH, ETAG_VALUE_0);
        return execution.execute(request, body);
      })
    );
  }

  public TestRestTemplate createVisitorClient() {
    TestRestTemplate visitorClient = new TestRestTemplate();
    setHeader(visitorClient, null);
    return visitorClient;
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
    setHeader(authenticatedClient, token);
    return authenticatedClient;
  }
}

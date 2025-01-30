package com.gentlecorp.customer.config;

import com.gentlecorp.customer.testData.CustomerTestData;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TestClientProvider extends CustomerTestData {

  private final TestRestTemplate restTemplate;

  // Dynamisch URL mit dem Server-Port erstellen
  @Value("${server.port}")
  int serverPort;

    public TestClientProvider(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public TestRestTemplate adminClient;
  public TestRestTemplate userClient;
  public TestRestTemplate basicClient;
  public TestRestTemplate eliteClient;
  public TestRestTemplate supremeClient;
  public TestRestTemplate visitorClient;


  //@PostConstruct
  public void init(final int port) {
    serverPort = port;
    adminClient = createAuthenticatedClient(ROLE_ADMIN, ROLE_PASSWORD);
    userClient = createAuthenticatedClient(ROLE_USER, ROLE_PASSWORD);
    basicClient = createAuthenticatedClient(ROLE_BASIC, ROLE_PASSWORD);
    eliteClient = createAuthenticatedClient(ROLE_ELITE, ROLE_PASSWORD);
    supremeClient = createAuthenticatedClient(ROLE_SUPREME, ROLE_PASSWORD);
    visitorClient = createVisitorClient();
  }

  private TestRestTemplate createVisitorClient() {
    TestRestTemplate visitorClient = new TestRestTemplate();
    // Basis-URL prüfen, falls für spezifische Anfragen notwendig
    String baseUrl = String.format("http://localhost:%s", serverPort);
    visitorClient.getRestTemplate().setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
    return visitorClient;
  }

  public TestRestTemplate createAuthenticatedClient(String username, String password) {
      String loginUrl = String.format("http://localhost:%s%s", serverPort, LOGIN_PATH);

    ResponseEntity<Map> response = restTemplate.postForEntity(
        loginUrl,
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

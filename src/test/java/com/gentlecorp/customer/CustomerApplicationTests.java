package com.gentlecorp.customer;

import com.gentlecorp.customer.controller.QueryController;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

//@Import(TestcontainersConfiguration.class)
@Import(Env.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CustomerApplicationTests {

	// Statischer Initialisierungsblock für .envs
	@BeforeAll
	static void loadEnv() {
		new Env();
	}

	private static final String SCHEMA_HOST = "http://localhost:";
	private static final String GRAPHQL_ENDPOINT = "/graphql";

	@Autowired
	private QueryController queryController;

	@Test
	void contextLoads() {
		assertThat(queryController).isNotNull();
	}

	@Value("${server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testGraphQlGreetingQuery() {
		String query = "{ \"query\": \"{ hallo }\" }";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(query, headers);

		String response = this.restTemplate.postForObject(SCHEMA_HOST + port + GRAPHQL_ENDPOINT, entity, String.class);

		assertThat(response).contains("Hello, GraphQL!");
	}
}
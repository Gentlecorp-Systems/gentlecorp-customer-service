package com.gentlecorp.customer;

import com.gentlecorp.customer.controller.CustomerReadController;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;


import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerApplicationTests {

	// Statischer Initialisierungsblock für .env
	@BeforeAll
	static void loadEnv() {
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
	}

	private static final String SCHEMA_HOST = "http://localhost:";

	@Autowired
	private CustomerReadController customerReadController;

	@Test
	void contextLoads() throws Exception {
		assertThat(customerReadController).isNotNull();
	}

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void greetingShouldReturnDefaultMessage() throws Exception {
		System.out.println(port);
		assertThat(this.restTemplate.getForObject(SCHEMA_HOST + port + CUSTOMER_PATH + "/hallo",
			String.class)).contains("Hallo");
	}
}
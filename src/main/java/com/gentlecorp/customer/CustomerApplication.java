package com.gentlecorp.customer;

import com.gentlecorp.customer.config.ApplicationConfig;
import com.gentlecorp.customer.config.EnvConfig;
import com.gentlecorp.customer.dev.DevConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static com.gentlecorp.customer.util.Banner.TEXT;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.hateoas.support.WebStack.WEBMVC;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, DevConfig.class})
@EnableConfigurationProperties({KeycloakProps.class, MailProps.class})
@EnableHypermediaSupport(type = HAL, stacks = WEBMVC)
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync
@EntityScan
@EnableMongoAuditing
public class CustomerApplication {

	public static void main(String[] args) {
		// Schlüsselwerte aus der EnvConfig abrufen
		String keycloakHost = EnvConfig.get("KEYCLOAK_HOST");
		String keycloakPort = EnvConfig.get("KEYCLOAK_PORT");

		System.out.printf("KEYCLOAK_HOST: %s%n", keycloakHost != null ? keycloakHost : "localhost");
		System.out.printf("KEYCLOAK_PORT: %s%n", keycloakPort != null ? keycloakPort : "8080");


		final var app = new SpringApplication(CustomerApplication.class);
		app.setBanner((_, _, out) -> out.println(TEXT));
		app.run(args);
	}

}

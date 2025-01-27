package com.gentlecorp.customer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProps(
  String schema,
  String host,
  int port,
  String clientId,
  String clientSecret
) {
}

package com.gentlecorp.customer.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

interface LogPasswordEncoding {
  Logger LOGGER = LoggerFactory.getLogger(LogPasswordEncoding.class);

  @Bean
  default ApplicationListener<ApplicationReadyEvent> logPasswordEncoding(
    final PasswordEncoder passwordEncoder,
    @Value("${app.password}") final String password
  ) {
    return event -> {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Argon2id with password \"{}\": {}", password, passwordEncoder.encode(password));
      }
    };
  }
}

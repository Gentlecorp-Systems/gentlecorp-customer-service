package com.gentlecorp.customer.dev;

import com.gentlecorp.customer.model.entity.Address;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.enums.RelationshipType;
import com.gentlecorp.customer.model.enums.StatusType;
import com.gentlecorp.customer.repository.AddressRepository;
import com.gentlecorp.customer.repository.ContactRepository;
import com.gentlecorp.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.Provider;
import java.security.Security;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface LogSignatureAlgorithms {
  @Bean
  @Profile("logSecurity")
  default ApplicationListener<ApplicationReadyEvent> logSignatureAlgorithms() {
    final var log = LoggerFactory.getLogger(LogSignatureAlgorithms.class);
    return event -> Arrays
      .stream(Security.getProviders())
      .forEach(provider -> logSignatureAlgorithms(provider, log));
  }

  private void logSignatureAlgorithms(final Provider provider, final Logger log) {
    provider
      .getServices()
      .forEach(service -> {
        if ("Signature".contentEquals(service.getType())) {
          log.debug("{}", service.getAlgorithm());
        }
      });
  }
}

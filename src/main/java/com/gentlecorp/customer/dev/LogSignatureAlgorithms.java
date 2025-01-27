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

  @Component
  class DatabaseInitializer implements CommandLineRunner {

      private final AddressRepository addressRepository;
      private final CustomerRepository customerRepository;
      private final ContactRepository contactRepository;

      public DatabaseInitializer(AddressRepository addressRepository, CustomerRepository customerRepository, ContactRepository contactRepository) {
          this.addressRepository = addressRepository;
          this.customerRepository = customerRepository;
          this.contactRepository = contactRepository;
      }

      @Override
      public void run(String... args) {
          initializeAddresses();
          initializeCustomers();
          initializeContacts();
      }

      private void initializeAddresses() {
          if (addressRepository.count() == 0) {
              List<Address> addresses = List.of(
                  Address.builder()
                      .street("Kwame Nkrumah Street")
                      .houseNumber("45")
                      .zipCode("KA003")
                      .city("Kumasi")
                      .state("Ashanti Region")
                      .country("Ghana")
                      .build(),
                  Address.builder()
                      .street("Namurstraße")
                      .houseNumber("4")
                      .zipCode("70374")
                      .city("Stuttgart")
                      .state("Baden Württemberg")
                      .country("Deutschland")
                      .build(),
                  Address.builder()
                      .street("Hauptstraße")
                      .houseNumber("15")
                      .zipCode("76135")
                      .city("Karlsruhe")
                      .state("Baden Württemberg")
                      .country("Deutschland")
                      .build(),
                  Address.builder()
                      .street("Mainzer Landstraße")
                      .houseNumber("50")
                      .zipCode("60329")
                      .city("Frankfurt")
                      .state("Hessen")
                      .country("Deutschland")
                      .build()
              );
              addressRepository.saveAll(addresses);
              System.out.println("Addresses initialized");
          }
      }

      private void initializeCustomers() {
          if (customerRepository.count() == 0) {
              List<Customer> customers = List.of(
                  Customer.builder()
                      .id(UUID.randomUUID())
                      .version(0)
                      .firstName("Admin")
                      .lastName("Caleb")
                      .email("admin@gentlecorp.com")
                      .phoneNumber("0000/0000000")
                      .tierLevel(3)
                      .subscribed(true)
                      .birthdate(LocalDate.of(1999, 5, 3))
                      .gender(GenderType.MALE)
                      .maritalStatus(MaritalStatusType.MARRIED)
                      .customerState(StatusType.ACTIVE)
                      .address(addressRepository.findByCity("Kumasi").stream()
                          .findFirst()
                          .orElseThrow(() -> new IllegalStateException("Address for 'Kumasi' not found")))
                      .contactOptions(List.of(ContactOptionsType.EMAIL))
                      .created(LocalDateTime.now())
                      .updated(LocalDateTime.now())
                      .username("admin")
                      .build(),
                  Customer.builder()
                      .id(UUID.randomUUID())
                      .version(0)
                      .firstName("Rachel")
                      .lastName("Gyamfi")
                      .email("racheldwomoh@icloud.com")
                      .phoneNumber("0000/0000002")
                      .tierLevel(3)
                      .subscribed(true)
                      .birthdate(LocalDate.of(1998, 12, 21))
                      .gender(GenderType.FEMALE)
                      .maritalStatus(MaritalStatusType.MARRIED)
                      .customerState(StatusType.ACTIVE)
                      .address(addressRepository.findByCity("Stuttgart").stream()
                          .findFirst()
                          .orElseThrow(() -> new IllegalStateException("Address for 'Stuttgart' not found")))
                      .contactOptions(List.of(ContactOptionsType.LETTER))
                      .created(LocalDateTime.now())
                      .updated(LocalDateTime.now())
                      .username("rae")
                      .build()
              );
              customerRepository.saveAll(customers);
              System.out.println("Customers initialized");
          }
      }

      private void initializeContacts() {
          if (contactRepository.count() == 0) {
              List<Contact> contacts = List.of(
                  Contact.builder()
                      .customerId(UUID.randomUUID())
                      .relationship(RelationshipType.PARTNER)
                      .withdrawalLimit(1000)
                      .emergencyContact(true)
                      .startDate(LocalDate.of(2020, 1, 1))
                      .build(),
                  Contact.builder()
                      .customerId(UUID.randomUUID())
                      .relationship(RelationshipType.CHILD)
                      .withdrawalLimit(500)
                      .emergencyContact(false)
                      .startDate(LocalDate.of(2020, 1, 1))
                      .build()
              );
              contactRepository.saveAll(contacts);
              System.out.println("Contacts initialized");
          }
      }
  }
}

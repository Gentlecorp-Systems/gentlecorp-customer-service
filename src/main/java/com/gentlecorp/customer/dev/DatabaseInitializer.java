package com.gentlecorp.customer.dev;

import com.gentlecorp.customer.model.entity.Address;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.*;
import com.gentlecorp.customer.repository.AddressRepository;
import com.gentlecorp.customer.repository.ContactRepository;
import com.gentlecorp.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;

    public void run() {
        initializeAddresses();
        initializeCustomers();
        initializeContacts();
    }

    private void initializeAddresses() {
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

    private void initializeCustomers() {
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

    private void initializeContacts() {
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

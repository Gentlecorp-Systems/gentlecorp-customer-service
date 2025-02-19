package com.gentlecorp.customer.util;

import com.gentlecorp.customer.exception.ConstraintViolationsException;
import com.gentlecorp.customer.exception.ContactExistsException;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class Validation {
  private final Validator validator;

  public <T> void validateDTO(T dto, Class<?>... groups) {
    // Standard-Validierung ausführen
    final Set<ConstraintViolation<T>> violations = validator.validate(dto, groups);

    // 🔥 Hier wird auch klassenbezogene Validierung berücksichtigt!
    final Set<ConstraintViolation<T>> classLevelViolations = validator.validate(dto);

    // Beide Validierungen zusammenführen
    violations.addAll(classLevelViolations);

    if (!violations.isEmpty()) {
      log.debug("🚨 Validation failed: {}", violations);

      if (dto instanceof CustomerDTO) {
        @SuppressWarnings("unchecked")
        var customerViolations = new ArrayList<>((Collection<ConstraintViolation<CustomerDTO>>) (Collection<?>) violations);
        throw new ConstraintViolationsException(customerViolations, null);
      }

      if (dto instanceof ContactDTO) {
        @SuppressWarnings("unchecked")
        var contactViolations = new ArrayList<>((Collection<ConstraintViolation<ContactDTO>>) (Collection<?>) violations);
        throw new ConstraintViolationsException(null, contactViolations);
      }
    }
  }



  public static void validateContact(Contact newContact, List<Contact> contacts) {
    contacts.forEach(
      contact -> {
        if (contact.getLastName().equals(newContact.getLastName()) && contact.getFirstName().equals(newContact.getFirstName())) {
          throw new ContactExistsException(contact.getLastName(), contact.getFirstName());
        }
      });
  }

  public static void validateContact(Contact newContact, Contact existingContact, final UUID contactId) {
    if (existingContact == null) {
      return;
    }

    if (existingContact.getId().equals(contactId)) {
      log.error("Contact with id {} already exists", contactId);
      return;
    }

    if (existingContact.getFirstName().equals(newContact.getFirstName()) && existingContact.getLastName().equals(newContact.getLastName())) {
      log.error("Contact with name {} already exists", newContact.getFirstName());
      throw new ContactExistsException(newContact.getLastName(), newContact.getFirstName());
    }
  }
}

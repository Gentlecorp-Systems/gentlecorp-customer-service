package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ConstraintViolationsException extends RuntimeException {
  private final transient Collection<ConstraintViolation<CustomerDTO>> customerViolationsDTO;
  private final transient Collection<ConstraintViolation<ContactDTO>> contactViolationsDTO;

  public ConstraintViolationsException(
    final Collection<ConstraintViolation<CustomerDTO>> customerViolations,
    final Collection<ConstraintViolation<ContactDTO>> contactViolations
  ) {
    super(formatMessage(customerViolations, contactViolations));
    this.customerViolationsDTO = customerViolations;
    this.contactViolationsDTO = contactViolations;
  }

  private static String formatMessage(
    Collection<ConstraintViolation<CustomerDTO>> customerViolations,
    Collection<ConstraintViolation<ContactDTO>> contactViolations
  ) {
    String customerMessage = customerViolations != null ? customerViolations.stream()
      .map(violation -> String.format(
        "'%s': %s",
        violation.getPropertyPath(),
        violation.getMessage()
      ))
      .collect(Collectors.joining(" | ")) : "";

    String contactMessage = contactViolations != null ? contactViolations.stream()
      .map(violation -> String.format(
        "'%s': %s",
        violation.getPropertyPath(),
        violation.getMessage()
      ))
      .collect(Collectors.joining(" | ")) : "";

    return Stream.of(customerMessage, contactMessage)
      .filter(msg -> !msg.isBlank())
      .collect(Collectors.joining(" | "));
  }
}

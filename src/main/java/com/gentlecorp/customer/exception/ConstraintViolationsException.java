package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Diese Ausnahme wird ausgelöst, wenn eine oder mehrere Validierungsregeln verletzt wurden.
 * <p>
 * Die Exception enthält Listen mit fehlerhaften Feldern aus den Entitäten `CustomerDTO` und `ContactDTO`.
 * </p>
 *
 * @since 13.02.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class ConstraintViolationsException extends RuntimeException {
  /** Liste der Validierungsfehler für die `CustomerDTO`-Entität. */
  private final transient Collection<ConstraintViolation<CustomerDTO>> customerViolationsDTO;
  /** Liste der Validierungsfehler für die `ContactDTO`-Entität. */
  private final transient Collection<ConstraintViolation<ContactDTO>> contactViolationsDTO;

  /**
   * Erstellt eine neue `ConstraintViolationsException` mit einer Liste von Validierungsfehlern.
   *
   * @param customerViolations Liste der Kunden-Validierungsfehler.
   * @param contactViolations  Liste der Kontakt-Validierungsfehler.
   */
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

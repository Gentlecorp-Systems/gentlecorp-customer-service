package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ausnahme für Validierungsfehler.
 * <p>
 * Wird ausgelöst, wenn eine oder mehrere Validierungsregeln verletzt wurden.
 * Die Exception enthält Listen mit fehlerhaften Feldern für `CustomerDTO` und `ContactDTO`.
 * </p>
 *
 * @since 13.02.2024
 * @version 1.3
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class ConstraintViolationsException extends RuntimeException {

  /** Liste der Validierungsfehler für die `CustomerDTO`-Entität. */
  private final transient List<ConstraintViolation<CustomerDTO>> customerViolations;

  /** Liste der Validierungsfehler für die `ContactDTO`-Entität. */
  private final transient List<ConstraintViolation<ContactDTO>> contactViolations;

  /**
   * Erstellt eine neue `ConstraintViolationsException` mit Listen von Validierungsfehlern.
   *
   * @param customerViolations Liste der Kunden-Validierungsfehler.
   * @param contactViolations  Liste der Kontakt-Validierungsfehler.
   */
  public ConstraintViolationsException(
      final List<ConstraintViolation<CustomerDTO>> customerViolations,
      final List<ConstraintViolation<ContactDTO>> contactViolations
  ) {
    super(formatMessage(customerViolations, contactViolations));
    this.customerViolations = customerViolations != null ? customerViolations : List.of();
    this.contactViolations = contactViolations != null ? contactViolations : List.of();
  }

  /**
   * Formatiert die Validierungsfehler als lesbare Zeichenkette.
   *
   * @param customerViolations Liste der `CustomerDTO`-Fehlermeldungen.
   * @param contactViolations  Liste der `ContactDTO`-Fehlermeldungen.
   * @return Formatierte Zeichenkette mit allen Fehlern.
   */
  private static String formatMessage(
      List<ConstraintViolation<CustomerDTO>> customerViolations,
      List<ConstraintViolation<ContactDTO>> contactViolations
  ) {
    System.out.println("formatMessage: " + customerViolations);
    System.out.println("formatMessage: " + contactViolations);

    String customerMessage = customerViolations != null && !customerViolations.isEmpty()
        ? "Fehler in CustomerDTO:\n" + formatViolations(customerViolations)
        : "";

    String contactMessage = contactViolations != null && !contactViolations.isEmpty()
        ? "Fehler in ContactDTO:\n" + formatViolations(contactViolations)
        : "";

    return Stream.of(customerMessage, contactMessage)
        .filter(msg -> !msg.isBlank())
        .collect(Collectors.joining("\n\n"));
  }

  /**
   * Formatiert eine Liste von `ConstraintViolation` in eine menschenlesbare Form.
   * Zeigt zusätzlich den ungültigen Wert in der Fehlermeldung an.
   *
   * @param violations Liste der Constraint-Verletzungen.
   * @return Formatierte Fehlerliste als Zeichenkette.
   */
  private static String formatViolations(List<? extends ConstraintViolation<?>> violations) {
    return violations.stream()
        .map(violation -> {
          Object invalidValue = violation.getInvalidValue();
          String valueString = invalidValue != null ? String.format("\"%s\"", invalidValue) : "null";
          return String.format("  - Feld '%s': %s: %s",
              violation.getPropertyPath(), violation.getMessage(), valueString);
        })
        .collect(Collectors.joining("\n"));
  }

  /**
   * Überschreibt `toString()` für bessere Fehlermeldungen im Log.
   *
   * @return Formatierte Zeichenkette mit allen Fehlern.
   */
  @Override
  public String toString() {
    return String.format("ValidationException:\n%s", getMessage());
  }
}

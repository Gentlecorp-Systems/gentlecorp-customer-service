package com.gentlecorp.customer.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Ausnahme, die ausgelöst wird, wenn ein Kunde nicht gefunden werden kann.
 * <p>
 * Diese Ausnahme signalisiert, dass kein Kunde mit der angegebenen ID oder Suchkriterien existiert.
 * </p>
 *
 * @since 13.02.2024
 * @version 1.1
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public final class NotFoundException extends RuntimeException {

  /**
   * Die ID des nicht gefundenen Kunden.
   * <p>
   * Wird verwendet, wenn eine bestimmte Kunden-ID nicht existiert.
   * </p>
   */
  private final UUID id;

  /**
   * Die Suchkriterien, die keine Ergebnisse geliefert haben.
   * <p>
   * Wird verwendet, wenn keine Kunden anhand der angegebenen Suchkriterien gefunden wurden.
   * </p>
   */
  private final Map<String, List<String>> searchCriteria;

  /**
   * Die Nachricht, die die Details zur Ausnahme enthält.
   */
  private final String message;

  /**
   * Erstellt eine neue {@code NotFoundException}, wenn ein Kunde mit der angegebenen ID nicht gefunden wurde.
   *
   * @param id Die ID des nicht gefundenen Kunden.
   */
  public NotFoundException(final UUID id) {
    super(String.format("Kein Kunde mit der ID %s gefunden.", id));
    this.id = id;
    this.searchCriteria = null;
    this.message = String.format("Kein Kunde mit der ID %s gefunden.", id);
  }

  /**
   * Erstellt eine neue {@code NotFoundException}, wenn keine Kunden mit den angegebenen Suchkriterien gefunden wurden.
   *
   * @param searchCriteria Die Suchkriterien, die keine Ergebnisse geliefert haben.
   */
  public NotFoundException(final Map<String, List<String>> searchCriteria) {
    super(String.format("Keine Kunden mit diesen Suchkriterien gefunden: %s", searchCriteria));
    this.id = null;
    this.searchCriteria = searchCriteria;
    this.message = String.format("Keine Kunden mit diesen Suchkriterien gefunden: %s", searchCriteria);
  }

  /**
   * Erstellt eine neue {@code NotFoundException}, wenn ein Kunde mit einem bestimmten Benutzernamen nicht gefunden wurde.
   *
   * @param username Der Benutzername des nicht gefundenen Kunden.
   */
  public NotFoundException(final String username) {
    super(String.format("Kein Kunde mit dem Benutzernamen '%s' gefunden.", username));
    this.id = null;
    this.searchCriteria = null;
    this.message = String.format("Kein Kunde mit dem Benutzernamen '%s' gefunden.", username);
  }

  /**
   * Erstellt eine neue {@code NotFoundException} mit einer Standardnachricht, wenn keine Kunden gefunden wurden.
   */
  public NotFoundException() {
    super("Keine Kunden gefunden.");
    this.id = null;
    this.searchCriteria = null;
    this.message = "Keine Kunden gefunden.";
  }
}

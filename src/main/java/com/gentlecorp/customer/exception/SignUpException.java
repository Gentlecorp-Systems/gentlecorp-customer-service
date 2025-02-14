package com.gentlecorp.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ausnahme, die während eines fehlgeschlagenen Registrierungsversuchs ausgelöst wird.
 * <p>
 * Diese Ausnahme wird verwendet, wenn eine Anmeldung aufgrund ungültiger oder unvollständiger Daten fehlschlägt.
 * Sie führt zu einer HTTP 500 (Internal Server Error) Antwort.
 * </p>
 *
 * @since 13.02.2025
 * @version 1.1
 * @author Caleb Gyamfi
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SignUpException extends RuntimeException {

  /**
   * Erstellt eine neue {@code SignUpException} mit einer bestimmten Fehlermeldung.
   *
   * @param message Die Detailnachricht, die den Grund der Ausnahme erklärt.
   */
  public SignUpException(final String message) {
    super(message);
  }
}

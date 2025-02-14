package com.gentlecorp.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ausnahme, die ausgelöst wird, wenn ein nicht autorisierter Zugriff versucht wird.
 * <p>
 * Diese Ausnahme wird verwendet, um anzuzeigen, dass eine Operation oder Anfrage nicht
 * durchgeführt werden konnte, da die erforderlichen Berechtigungen fehlen.
 * Sie resultiert in einer HTTP 401 (Unauthorized) Antwort.
 * </p>
 *
 * @since 13.02.2024
 * @version 1.1
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

  /**
   * Erstellt eine neue `UnauthorizedException` mit einer bestimmten Fehlermeldung.
   *
   * @param message Die Detailnachricht, die den Grund der Ausnahme erklärt.
   */
  public UnauthorizedException(final String message) {
    super(message);
  }
}

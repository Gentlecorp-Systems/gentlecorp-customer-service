package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Ausnahme, die ausgelöst wird, wenn eine angegebene Version über der aktuellen liegt.
 * <p>
 * Diese Ausnahme wird genutzt, um anzuzeigen, dass eine angegebene Versionsnummer noch nicht gültig ist.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Getter
public class VersionAheadException extends RuntimeException {

  /** Die ungültige Versionsnummer. */
  private final int version;

  /**
   * Erstellt eine neue `VersionAheadException` mit der angegebenen Version.
   *
   * @param version Die nicht gültige Version.
   */
  public VersionAheadException(int version) {
    super(String.format("Die angegebene Version %d ist voraus und noch nicht gültig.", version));
    this.version = version;
  }
}

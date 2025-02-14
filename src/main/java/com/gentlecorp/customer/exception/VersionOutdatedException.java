package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Ausnahme, die ausgelöst wird, wenn eine angegebene Version veraltet ist.
 * <p>
 * Diese Ausnahme signalisiert, dass die verwendete Version nicht mehr gültig ist.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Getter
public class VersionOutdatedException extends RuntimeException {

  /** Die veraltete Versionsnummer. */
  private final int version;

  /**
   * Erstellt eine neue `VersionOutdatedException` mit einer bestimmten Version.
   *
   * @param version Die veraltete Version.
   */
  public VersionOutdatedException(final int version) {
    super("Die Versionsnummer " + version + " ist veraltet.");
    this.version = version;
  }
}

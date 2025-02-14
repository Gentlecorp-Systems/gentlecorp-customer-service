package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Ausnahme, die ausgelöst wird, wenn ein Benutzername bereits existiert.
 * <p>
 * Diese Ausnahme signalisiert, dass ein Benutzername während der Registrierung oder Aktualisierung
 * bereits von einem anderen Benutzer verwendet wird, um doppelte Benutzernamen im System zu verhindern.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Getter
public class UsernameExistsException extends RuntimeException {

  /** Der Benutzername, der bereits existiert. */
  private final String username;

  /**
   * Erstellt eine neue `UsernameExistsException` mit einem bestimmten Benutzernamen.
   *
   * @param username Der bereits existierende Benutzername.
   */
  public UsernameExistsException(final String username) {
    super("Der Benutzername " + username + " existiert bereits.");
    this.username = username;
  }
}

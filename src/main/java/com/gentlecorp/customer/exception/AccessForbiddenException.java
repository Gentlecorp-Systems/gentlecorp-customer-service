package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.security.enums.RoleType;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Diese Ausnahme wird ausgelöst, wenn ein Benutzer nicht über ausreichende Berechtigungen verfügt.
 * <p>
 * Sie enthält Informationen über den Benutzer und seine aktuellen Rollen.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Getter
public class AccessForbiddenException extends RuntimeException  {

  /** Der Benutzer, der keinen Zugriff hat. */
  private final String user;

  /** Vorhandene Rollen des Benutzers.*/
  private final Collection<RoleType> roles;

  /**
   * Erstellt eine neue `AccessForbiddenException` mit Benutzername und Rollenliste.
   *
   * @param user  Der Name des Benutzers.
   * @param roles Die Rollen des Benutzers.
   */
  public AccessForbiddenException(final String user, final Collection<RoleType> roles) {
    super(String.format("Zugriff verweigert: Benutzer '%s' besitzt nur die Rollen [%s], die für diese Anfrage nicht ausreichen.",
        user, roles.stream().map(Enum::name).collect(Collectors.joining(", "))));

    this.user = user;
    this.roles = roles;
  }
}

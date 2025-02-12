package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.security.RoleType;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class AccessForbiddenException extends RuntimeException {

  private final String user;

  /**
   * Vorhandene Rollen des Benutzers.
   */
  private final Collection<RoleType> roles;

  @SuppressWarnings("ParameterHidesMemberVariable")
  public AccessForbiddenException(final String user, final Collection<RoleType> roles) {
    super(String.format("Zugriff verweigert: Benutzer '%s' besitzt nur die Rollen [%s], die für diese Anfrage nicht ausreichen.",
        user, roles.stream().map(Enum::name).collect(Collectors.joining(", "))));

    this.user = user;
    this.roles = roles;
  }
}

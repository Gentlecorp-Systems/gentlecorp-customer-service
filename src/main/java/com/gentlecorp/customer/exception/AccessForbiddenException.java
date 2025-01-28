package com.gentlecorp.customer.exception;

import lombok.Getter;

@Getter
public class AccessForbiddenException extends RuntimeException {
  private final String role;

  @SuppressWarnings("ParameterHidesMemberVariable")
  public AccessForbiddenException(final String role) {
    super(String.format("Unzureichende RoleType als: %s", role));
    this.role = role;
  }
}

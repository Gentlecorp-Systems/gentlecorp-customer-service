package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum RoleType {
  ADMIN("admin"),
  SUPREME("supreme"),
  BASIC("basic"),
  ELITE("elite"),
  USER("user");

  public static final String ROLE_PREFIX = "ROLE_";

  private final String role;

  public static RoleType of(final String str) {
    return Stream.of(values())
      .filter(role -> role.role.equalsIgnoreCase(str))
      .findFirst()
      .orElse(null);
  }

  @JsonValue
  public String getRole() {
    return role;
  }
}

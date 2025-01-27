package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für verschiedene Benutzerrollen in der Anwendung.
 * Unterstützt JSON- und MongoDB-Verarbeitung.
 */
@RequiredArgsConstructor
public enum RoleType {

  ADMIN("admin"),
  SUPREME("supreme"),
  BASIC("basic"),
  ELITE("elite"),
  USER("user");

  public static final String ROLE_PREFIX = "ROLE_";

  private final String role;

  /**
   * Gibt die String-Repräsentation des Rollenwerts zurück.
   *
   * @return die String-Repräsentation der Rolle.
   */
  @JsonValue
  public String getRole() {
    return role;
  }

  /**
   * Erstellt einen Enum-Wert aus einem String-Wert.
   * Unterstützt JSON- und MongoDB-Datenverarbeitung.
   *
   * @param value der String-Wert der Rolle.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static RoleType fromValue(final String value) {
    return Arrays.stream(values())
        .filter(role -> role.role.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für RoleType", value)
        ));
  }

  /**
   * Gibt die Rolle mit einem vorangestellten Präfix zurück.
   *
   * @return die Rolle mit dem Präfix 'ROLE_'.
   */
  public String getPrefixedRole() {
    return ROLE_PREFIX + role.toUpperCase();
  }
}

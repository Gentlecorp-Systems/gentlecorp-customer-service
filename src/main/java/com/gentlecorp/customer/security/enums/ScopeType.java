package com.gentlecorp.customer.security.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Enum zur Definition von Geltungsbereichen (Scopes) für Zugriffstoken.
 * <p>
 * Unterstützt JSON-Serialisierung und ermöglicht die Umwandlung von String-Werten in Enum-Werte.
 * </p>
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public enum ScopeType {
  ALL_PROFILE("openid profile email"),
  EMAIL_PROFILE("email profile");

  private final String value;

  /**
   * Konstruktor für ScopeType.
   *
   * @param value der String-Wert des Geltungsbereichs.
   */
  ScopeType(final String value) {
    this.value = value;
  }

  /**
   * Gibt die String-Repräsentation des Enum-Werts zurück.
   *
   * @return die String-Repräsentation des Geltungsbereichs.
   */
  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
   * Unterstützt JSON-Verarbeitung.
   *
   * @param value der String-Wert des Geltungsbereichs.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static ScopeType of(final String value) {
    return Arrays.stream(values())
        .filter(scope -> scope.value.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für ScopeType", value)
        ));
  }
}

package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Enum für Token-Typen, unterstützt JSON- und Datenbank-Verarbeitung.
 */
public enum TokenType {
  BEARER("Bearer");

  private final String value;

  /**
   * Konstruktor für TokenType.
   *
   * @param value der String-Wert des Token-Typs.
   */
  TokenType(final String value) {
    this.value = value;
  }

  /**
   * Gibt die String-Repräsentation des Token-Typs zurück.
   *
   * @return der Token-Typ als String.
   */
  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
   * Unterstützt JSON- und Datenbank-Verarbeitung.
   *
   * @param value der String-Wert des Token-Typs.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static TokenType of(final String value) {
    return Arrays.stream(values())
        .filter(token -> token.value.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für TokenType", value)
        ));
  }
}

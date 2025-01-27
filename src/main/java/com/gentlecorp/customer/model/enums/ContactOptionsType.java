package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für Kontaktoptionen, angepasst für die Verwendung mit MongoDB.
 */
@RequiredArgsConstructor
public enum ContactOptionsType {

  EMAIL("E"),
  PHONE("P"),
  LETTER("L"),
  SMS("S");

  private final String option;

  /**
   * Gibt die String-Repräsentation des Enums zurück.
   *
   * @return die String-Repräsentation der Option.
   */
  @JsonValue
  public String getOption() {
    return option;
  }

  /**
   * Erzeugt einen Enum-Wert aus einem String-Wert.
   * Unterstützt sowohl Jackson für JSON-Daten als auch MongoDB.
   *
   * @param value die String-Repräsentation der Option.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static ContactOptionsType fromValue(final String value) {
    return Arrays.stream(values())
        .filter(option -> option.option.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für ContactOptionsType", value)
        ));
  }
}

package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für Familienstandstypen, angepasst für JSON und MongoDB.
 */
@RequiredArgsConstructor
public enum MaritalStatusType {

  SINGLE("S"),
  MARRIED("M"),
  DIVORCED("D"),
  WIDOWED("W");

  private final String status;

  /**
   * Gibt die String-Repräsentation des Enum-Werts zurück.
   *
   * @return die String-Repräsentation des Familienstandes.
   */
  @JsonValue
  public String getStatus() {
    return status;
  }

  /**
   * Erstellt einen Enum-Wert aus einem String-Wert.
   * Unterstützt JSON- und MongoDB-Datenverarbeitung.
   *
   * @param value der String-Wert des Familienstandes.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static MaritalStatusType fromValue(final String value) {
    return Arrays.stream(values())
        .filter(statusType -> statusType.status.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für MaritalStatusType", value)
        ));
  }
}

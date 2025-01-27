package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für Geschlechtertypen, angepasst für die Verwendung mit JSON und MongoDB.
 */
@RequiredArgsConstructor
public enum GenderType {

  MALE("M"),
  FEMALE("F"),
  DIVERSE("D");

  private final String gender;

  /**
   * Gibt die String-Repräsentation des Enum-Werts zurück.
   *
   * @return die String-Repräsentation.
   */
  @JsonValue
  public String getGender() {
    return gender;
  }

  /**
   * Erstellt einen Enum-Wert aus einem String-Wert.
   * Unterstützt die Verarbeitung von JSON und MongoDB-Daten.
   *
   * @param value der String-Wert des Geschlechts.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static GenderType fromValue(final String value) {
    return Arrays.stream(values())
        .filter(genderType -> genderType.gender.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für GenderType", value)
        ));
  }
}

package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Enum für Kontaktoptionen, angepasst für die Verwendung mit MongoDB und JSON.
 */
@RequiredArgsConstructor
public enum ContactOptionsType {

  EMAIL("E", "EMAIL"),
  PHONE("P", "PHONE"),
  LETTER("L", "LETTER"),
  SMS("S", "SMS");

  private final String shortValue;
  private final String longValue;

  /**
   * Gibt die JSON-kompatible String-Repräsentation zurück.
   *
   * @return die Langform der Kontaktoption als String.
   */
  @JsonValue
  public String getJsonValue() {
    return longValue; // Gibt die Langform aus, z. B. "EMAIL"
  }

  private static final Map<String, ContactOptionsType> lookup = Stream.of(values())
      .collect(java.util.stream.Collectors.toMap(
          e -> e.shortValue.toUpperCase(), e -> e
      ));

  /**
   * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
   * Unterstützt sowohl "E", "P", "L", "S" als auch "EMAIL", "PHONE", "LETTER", "SMS".
   *
   * @param value der String-Wert der Kontaktoption.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static ContactOptionsType fromValue(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("ContactOptionsType darf nicht null sein.");
    }

    String upperValue = value.toUpperCase();
    for (ContactOptionsType type : values()) {
      if (type.shortValue.equalsIgnoreCase(upperValue) || type.longValue.equalsIgnoreCase(upperValue)) {
        return type;
      }
    }

    throw new IllegalArgumentException(
        String.format("Ungültiger Wert '%s' für ContactOptionsType. Erlaubt: E, P, L, S oder EMAIL, PHONE, LETTER, SMS", value)
    );
  }
}

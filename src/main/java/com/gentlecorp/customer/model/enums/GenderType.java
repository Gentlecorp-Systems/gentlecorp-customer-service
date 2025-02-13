package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Enum für Geschlechtertypen, angepasst für JSON und MongoDB.
 */
@RequiredArgsConstructor
public enum GenderType {

  MALE("M", "MALE"),
  FEMALE("F", "FEMALE"),
  DIVERSE("D", "DIVERSE");

  private final String shortValue;
  private final String longValue;

  /**
   * Gibt die String-Repräsentation des Enum-Werts zurück.
   *
   * @return die String-Repräsentation für JSON.
   */
  @JsonValue
  public String getJsonValue() {
    return longValue;
  }

  private static final Map<String, GenderType> lookup = Stream.of(values())
      .collect(java.util.stream.Collectors.toMap(
          g -> g.shortValue.toUpperCase(), g -> g
      ));

  /**
   * Erstellt einen Enum-Wert aus einem String-Wert.
   * Unterstützt "M", "F", "D" sowie "MALE", "FEMALE", "DIVERSE".
   *
   * @param value der String-Wert des Geschlechts.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static GenderType fromValue(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("GenderType darf nicht null sein.");
    }

    String upperValue = value.toUpperCase();
    for (GenderType type : values()) {
      if (type.shortValue.equalsIgnoreCase(upperValue) || type.longValue.equalsIgnoreCase(upperValue)) {
        return type;
      }
    }

    throw new IllegalArgumentException(
        String.format("Ungültiger Wert '%s' für GenderType. Erlaubt: M, F, D oder MALE, FEMALE, DIVERSE", value)
    );
  }
}

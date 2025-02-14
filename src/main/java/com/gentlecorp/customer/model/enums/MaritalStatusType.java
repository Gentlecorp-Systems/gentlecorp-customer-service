package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Enum für Familienstandstypen eines Kunden.
 * <p>
 * Unterstützt Kurzformen ("S", "M", "D", "W") sowie Langformen ("SINGLE", "MARRIED", "DIVORCED", "WIDOWED").
 * Optimiert für JSON-Serialisierung.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@RequiredArgsConstructor
public enum MaritalStatusType {

  SINGLE("S", "SINGLE"),
  MARRIED("M", "MARRIED"),
  DIVORCED("D", "DIVORCED"),
  WIDOWED("W", "WIDOWED");

  private final String shortValue;
  private final String longValue;

  /**
   * Gibt die String-Repräsentation des Enum-Werts zurück.
   *
   * @return die JSON-kompatible String-Repräsentation.
   */
  @JsonValue
  public String getJsonValue() {
    return longValue; // Gibt den langen Namen zurück, z. B. "SINGLE"
  }

  private static final Map<String, MaritalStatusType> lookup = Stream.of(values())
      .collect(java.util.stream.Collectors.toMap(
          e -> e.shortValue.toUpperCase(), e -> e
      ));

  /**
   * Erstellt einen Enum-Wert aus einem String-Wert.
   * Unterstützt "S", "M", "D", "W" sowie "SINGLE", "MARRIED", "DIVORCED", "WIDOWED".
   *
   * @param value der String-Wert des Familienstandes.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static MaritalStatusType fromValue(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("MaritalStatusType darf nicht null sein.");
    }

    String upperValue = value.toUpperCase();
    for (MaritalStatusType type : values()) {
      if (type.shortValue.equalsIgnoreCase(upperValue) || type.longValue.equalsIgnoreCase(upperValue)) {
        return type;
      }
    }

    throw new IllegalArgumentException(
        String.format("Ungültiger Wert '%s' für MaritalStatusType. Erlaubt: S, M, D, W oder SINGLE, MARRIED, DIVORCED, WIDOWED", value)
    );
  }
}

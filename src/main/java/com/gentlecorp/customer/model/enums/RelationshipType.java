package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Definiert verschiedene Beziehungstypen zwischen Kunden.
 * <p>
 * Dieses Enum wird verwendet, um die Art der Verbindung zwischen zwei Kunden darzustellen.
 * Es unterstützt sowohl JSON-Serialisierung als auch MongoDB-Speicherung.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@RequiredArgsConstructor
public enum RelationshipType {

  PARTNER("PN"),
  BUSINESS_PARTNER("BP"),
  RELATIVE("R"),
  COLLEAGUE("C"),
  PARENT("P"),
  SIBLING("S"),
  CHILD("CH"),
  COUSIN("CO");

  private final String relationship;

  /**
   * Gibt die String-Repräsentation des Beziehungstyps zurück.
   *
   * @return die String-Repräsentation des Beziehungstyps.
   */
  @JsonValue
  public String getRelationship() {
    return relationship;
  }

  /**
   * Erstellt einen Enum-Wert aus einem String-Wert.
   * Unterstützt JSON- und MongoDB-Datenverarbeitung.
   *
   * @param value der String-Wert des Beziehungstyps.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static RelationshipType fromValue(final String value) {
    return Arrays.stream(values())
        .filter(relationshipType -> relationshipType.relationship.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für RelationshipType", value)
        ));
  }
}

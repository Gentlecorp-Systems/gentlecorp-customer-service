package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für Beziehungstypen zwischen Kunden.
 * Unterstützt JSON- und MongoDB-Verarbeitung.
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

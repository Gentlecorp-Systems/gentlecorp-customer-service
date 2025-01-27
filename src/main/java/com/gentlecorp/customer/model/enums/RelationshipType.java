package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

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

  @JsonCreator
  public static RelationshipType of(final String value) {
    return Stream.of(values())
      .filter(relationshipType -> relationshipType.relationship.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }

  @JsonValue
  public String getRelationship() {
    return relationship;
  }
}

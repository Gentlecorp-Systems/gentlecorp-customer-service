package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum GenderType {
  MALE("M"),
  FEMALE("F"),
  DIVERSE("D");

  private final String gender;

  @JsonCreator
  public static GenderType of(final String value) {
    return Stream.of(values())
      .filter(genderType -> genderType.gender.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }

  @JsonValue
  public String getGender() {
    return gender;
  }
}

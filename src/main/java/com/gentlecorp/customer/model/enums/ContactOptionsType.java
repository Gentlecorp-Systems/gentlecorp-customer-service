package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ContactOptionsType {
  EMAIL("E"),
  PHONE("P"),
  LETTER("L"),
  SMS("S");

  private final String option;

  @JsonCreator
  public static ContactOptionsType of(final String value) {
    return Stream.of(values())
      .filter(optionsType -> optionsType.option.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
  @JsonValue
  public String getOption() {
    return option;
  }
}

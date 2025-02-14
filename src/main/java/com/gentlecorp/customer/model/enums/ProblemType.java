package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enum für verschiedene Problemtypen in der Anwendung, z. B. Fehlerarten oder Statuscodes.
 * <p>
 * Dieses Enum definiert die Problemtypen, die zur Fehlerbehandlung in der Anwendung verwendet werden.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Getter
public enum ProblemType {
  CONSTRAINTS("constraints"),
  UNPROCESSABLE("unprocessable"),
  PRECONDITION("precondition"),
  BAD_REQUEST("badRequest"),
  FORBIDDEN("forbidden"),
  CONFLICT("conflict"),
  NOT_FOUND("notFound");

  private final String value;

  ProblemType(final String value) {
    this.value = value;
  }

  /**
   * Gibt die String-Repräsentation des Enum-Werts zurück.
   *
   * @return die String-Repräsentation des Problemtyps.
   */
  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
   * Unterstützt JSON- und MongoDB-Datenverarbeitung.
   *
   * @param value der String-Wert des Problemtyps.
   * @return der entsprechende Enum-Wert.
   * @throws IllegalArgumentException wenn der Wert ungültig ist.
   */
  @JsonCreator
  public static ProblemType fromValue(final String value) {
    return Arrays.stream(values())
        .filter(problemType -> problemType.value.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für ProblemType", value)
        ));
  }
}

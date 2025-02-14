package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Definiert die möglichen Statusarten eines Kunden.
 * <p>
 * Unterstützt Kurzformen ("A", "B", "I", "C") sowie Langformen ("ACTIVE", "BLOCKED", "INACTIVE", "CLOSED").
 * Optimiert für JSON-Verarbeitung.
 * </p>
 *
 * @author Caleb Gyamfi
 * @version 1.0
 */
@RequiredArgsConstructor
public enum StatusType {

    ACTIVE("A", "ACTIVE"),
    BLOCKED("B", "BLOCKED"),
    INACTIVE("I", "INACTIVE"),
    CLOSED("C", "CLOSED");

    private final String shortValue;
    private final String longValue;

    /**
     * Gibt die JSON-kompatible String-Repräsentation des Status zurück.
     *
     * @return die lange Form des Status als String.
     */
    @JsonValue
    public String getJsonValue() {
        return longValue; // Gibt die Langform aus, z. B. "ACTIVE"
    }

    private static final Map<String, StatusType> lookup = Stream.of(values())
        .collect(java.util.stream.Collectors.toMap(
            e -> e.shortValue.toUpperCase(), e -> e
        ));

    /**
     * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
     * Unterstützt sowohl "A", "B", "I", "C" als auch "ACTIVE", "BLOCKED", "INACTIVE", "CLOSED".
     *
     * @param value der String-Wert des Status.
     * @return der entsprechende Enum-Wert.
     * @throws IllegalArgumentException wenn der Wert ungültig ist.
     */
    @JsonCreator
    public static StatusType of(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("StatusType darf nicht null sein.");
        }

        String upperValue = value.toUpperCase();
        for (StatusType type : values()) {
            if (type.shortValue.equalsIgnoreCase(upperValue) || type.longValue.equalsIgnoreCase(upperValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
            String.format("Ungültiger Statuswert '%s' für StatusType. Erlaubt: A, B, I, C oder ACTIVE, BLOCKED, INACTIVE, CLOSED", value)
        );
    }
}

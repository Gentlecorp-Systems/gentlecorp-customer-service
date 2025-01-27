package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für verschiedene Statusarten eines Kunden.
 * Unterstützt JSON-Verarbeitung.
 */
@RequiredArgsConstructor
public enum StatusType {

    ACTIVE("A"),
    BLOCKED("B"),
    INACTIVE("I"),
    CLOSED("C");

    private final String state;

    /**
     * Gibt die String-Repräsentation des Status zurück.
     *
     * @return der Statuswert als String.
     */
    @JsonValue
    public String getState() {
        return state;
    }

    /**
     * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
     * Unterstützt JSON- und Datenbank-Verarbeitung.
     *
     * @param value der String-Wert des Status.
     * @return der entsprechende Enum-Wert.
     * @throws IllegalArgumentException wenn der Wert ungültig ist.
     */
    @JsonCreator
    public static StatusType of(final String value) {
        return Arrays.stream(values())
            .filter(statusType -> statusType.state.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Ungültiger Statuswert '%s' für StatusType", value)
            ));
    }
}

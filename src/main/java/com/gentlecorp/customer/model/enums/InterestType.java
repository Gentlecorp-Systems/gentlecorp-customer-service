package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Enum für Interessentypen, angepasst für die Verwendung mit JSON und MongoDB.
 */
@RequiredArgsConstructor
public enum InterestType {

    INVESTMENTS("I", "INVESTMENTS"),
    SAVING_AND_FINANCE("SF", "SAVING_AND_FINANCE"),
    CREDIT_AND_DEBT("CD", "CREDIT_AND_DEBT"),
    BANK_PRODUCTS_AND_SERVICES("BPS", "BANK_PRODUCTS_AND_SERVICES"),
    FINANCIAL_EDUCATION_AND_COUNSELING("FEC", "FINANCIAL_EDUCATION_AND_COUNSELING"),
    REAL_ESTATE("RE", "REAL_ESTATE"),
    INSURANCE("IN", "INSURANCE"),
    SUSTAINABLE_FINANCE("SUF", "SUSTAINABLE_FINANCE"),
    TECHNOLOGY_AND_INNOVATION("IT", "TECHNOLOGY_AND_INNOVATION"),
    TRAVEL("T", "TRAVEL");

    private final String shortValue;
    private final String longValue;

    /**
     * Gibt die JSON-kompatible String-Repräsentation zurück.
     *
     * @return die Langform des Interesses als String.
     */
    @JsonValue
    public String getJsonValue() {
        return longValue;
    }

    private static final Map<String, InterestType> lookup = Stream.of(values())
        .collect(java.util.stream.Collectors.toMap(
            e -> e.shortValue.toUpperCase(), e -> e
        ));

    /**
     * Wandelt einen String-Wert in den entsprechenden Enum-Wert um.
     * Unterstützt Kurzform (I, SF, CD, ...) sowie Langform (INVESTMENTS, SAVING_AND_FINANCE, ...).
     *
     * @param value der String-Wert des Interesses.
     * @return der entsprechende Enum-Wert.
     * @throws IllegalArgumentException wenn der Wert ungültig ist.
     */
    @JsonCreator
    public static InterestType fromValue(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("InterestType darf nicht null sein.");
        }

        String upperValue = value.toUpperCase();
        for (InterestType type : values()) {
            if (type.shortValue.equalsIgnoreCase(upperValue) || type.longValue.equalsIgnoreCase(upperValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
            String.format("Ungültiger Wert '%s' für InterestType. Erlaubt sind Abkürzungen (I, SF, ...) oder Langformen (INVESTMENTS, SAVING_AND_FINANCE, ...)", value)
        );
    }
}

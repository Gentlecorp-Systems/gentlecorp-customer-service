package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum für Interessentypen, angepasst für die Verwendung mit JSON und MongoDB.
 */
@RequiredArgsConstructor
public enum InterestType {

    INVESTMENTS("I"),
    SAVING_AND_FINANCE("SF"),
    CREDIT_AND_DEBT("CD"),
    BANK_PRODUCTS_AND_SERVICES("BPS"),
    FINANCIAL_EDUCATION_AND_COUNSELING("FEC"),
    REAL_ESTATE("RE"),
    INSURANCE("IN"),
    SUSTAINABLE_FINANCE("SUF"),
    TECHNOLOGY_AND_INNOVATION("IT"),
    TRAVEL("T");

    private final String interest;

    /**
     * Gibt die String-Repräsentation des Enum-Werts zurück.
     *
     * @return die String-Repräsentation der Option.
     */
    @JsonValue
    public String getInterest() {
        return interest;
    }

    /**
     * Erstellt einen Enum-Wert aus einem String-Wert.
     * Unterstützt die Verarbeitung von JSON und MongoDB-Daten.
     *
     * @param value der String-Wert des Interesses.
     * @return der entsprechende Enum-Wert.
     * @throws IllegalArgumentException wenn der Wert ungültig ist.
     */
    @JsonCreator
    public static InterestType fromValue(final String value) {
        return Arrays.stream(values())
            .filter(interestType -> interestType.interest.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Ungültiger Wert '%s' für InterestType", value)
            ));
    }
}

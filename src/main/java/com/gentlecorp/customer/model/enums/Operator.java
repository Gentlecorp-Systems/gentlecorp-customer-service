package com.gentlecorp.customer.model.enums;

/**
 * Mögliche Vergleichsoperatoren für Filterbedingungen.
 */
public enum Operator {
    EQ,   // Gleichheit
    IN,   // Enthält eine der angegebenen Werte
    GTE,  // Größer oder gleich
    LTE,  // Kleiner oder gleich
    LIKE  // Teilstring-Suche
}

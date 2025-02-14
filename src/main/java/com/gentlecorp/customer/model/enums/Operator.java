package com.gentlecorp.customer.model.enums;

/**
 * Enum zur Definition von Vergleichsoperatoren für Filterbedingungen.
 * <p>
 * Diese Operatoren ermöglichen die Formulierung dynamischer Suchanfragen.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public enum Operator {
    EQ,   // Gleichheit
    IN,   // Enthält eine der angegebenen Werte
    GTE,  // Größer oder gleich
    LTE,  // Kleiner oder gleich
    LIKE  // Teilstring-Suche
}

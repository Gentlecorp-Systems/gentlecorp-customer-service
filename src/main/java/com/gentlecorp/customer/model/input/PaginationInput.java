package com.gentlecorp.customer.model.input;

/**
 * Record für die Paginierungseinstellungen von GraphQL-Abfragen.
 *
 * @param limit  Anzahl der zurückzugebenden Ergebnisse (Standard: 10).
 * @param offset Startpunkt der Ergebnisse (Standard: 0).
 */
public record PaginationInput(int limit, int offset) {
    public PaginationInput() {
        this(10, 0); // Standardwerte
    }
}

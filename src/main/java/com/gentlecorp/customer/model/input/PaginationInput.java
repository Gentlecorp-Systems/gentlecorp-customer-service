package com.gentlecorp.customer.model.input;

/**
 * Record zur Definition von Paginierungseinstellungen für GraphQL-Abfragen.
 * <p>
 * Ermöglicht die Begrenzung der zurückgegebenen Ergebnisse und die Angabe eines Startpunkts.
 * Standardwerte: `limit = 10`, `offset = 0`.
 * </p>
 *
 * @param limit  Anzahl der zurückzugebenden Ergebnisse (Standard: 10).
 * @param offset Startpunkt der Ergebnisse (Standard: 0).
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record PaginationInput(int limit, int offset) {
    public PaginationInput() {
        this(10, 0); // Standardwerte
    }
}

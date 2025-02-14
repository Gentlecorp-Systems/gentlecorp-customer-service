package com.gentlecorp.customer.model.input;

import com.gentlecorp.customer.model.enums.FilterOptions;
import com.gentlecorp.customer.model.enums.OrderDirection;

/**
 * Record zur Definition von Sortierparametern für GraphQL-Abfragen.
 * <p>
 * Ermöglicht die Angabe eines Sortierfelds und einer Sortierrichtung (aufsteigend/absteigend).
 * </p>
 *
 * @param field     Das Feld, nach dem sortiert werden soll.
 * @param direction Die Sortierrichtung (ASC oder DESC).
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record SortInput(FilterOptions field, OrderDirection direction) {
}

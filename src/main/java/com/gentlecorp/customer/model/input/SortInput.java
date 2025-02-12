package com.gentlecorp.customer.model.input;

import com.gentlecorp.customer.model.enums.FilterOptions;
import com.gentlecorp.customer.model.enums.OrderDirection;

/**
 * Record für die Sortierparameter von GraphQL-Abfragen.
 *
 * @param field     Das Feld, nach dem sortiert werden soll.
 * @param direction Die Sortierrichtung (ASC oder DESC).
 */
public record SortInput(FilterOptions field, OrderDirection direction) {
}

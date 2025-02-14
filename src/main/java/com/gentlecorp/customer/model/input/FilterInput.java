package com.gentlecorp.customer.model.input;

import com.gentlecorp.customer.model.enums.FilterOptions;
import com.gentlecorp.customer.model.enums.Operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Record zur Definition von Filterparametern für GraphQL-Abfragen.
 * <p>
 * Ermöglicht die dynamische Filterung von Abfragen mit verschiedenen Vergleichsoperatoren
 * sowie der Verknüpfung von Bedingungen über `AND`, `OR` und `NOR`.
 * </p>
 *
 * @param field    Das zu filternde Feld.
 * @param operator Der Vergleichsoperator (z. B. EQ, IN, GTE, LTE, LIKE).
 * @param value    Der Vergleichswert.
 * @param AND      Logische UND-Verknüpfung mit weiteren Filtern.
 * @param OR       Logische ODER-Verknüpfung mit weiteren Filtern.
 * @param NOR      Logische NOR-Verknüpfung mit weiteren Filtern.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record FilterInput(
    FilterOptions field,
    Operator operator,
    String value,
    List<FilterInput> AND,
    List<FilterInput> OR,
    List<FilterInput> NOR
) {
    /**
     * Konvertiert das `FilterInput` in eine Map für die Verwendung mit MongoDB.
     *
     * @return Eine Map mit den umgewandelten Filterkriterien.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> query = new HashMap<>();


        // Verarbeitung einer einzelnen Bedingung (z.B. gender=MALE)
        if (field != null && operator != null && value != null) {
            query.put(field.name(), Map.of("$" + operator.name().toLowerCase(), convertToNumberIfPossible(value)));
        }

        // Verarbeitung von AND-Bedingungen (MongoDB: `$and`)
        if (AND != null && !AND.isEmpty()) {
            query.put("$and", AND.stream().map(FilterInput::toMap).toList());
        }

        // Verarbeitung von OR-Bedingungen (MongoDB: `$or`)
        if (OR != null && !OR.isEmpty()) {
            query.put("$or", OR.stream().map(FilterInput::toMap).toList());
        }

        // Verarbeitung von NOR-Bedingungen (MongoDB: `$nor`)
        if (NOR != null && !NOR.isEmpty()) {
            query.put("$nor", NOR.stream().map(FilterInput::toMap).toList());
        }

        return query;
    }

    /**
     * Prüft, ob der Wert eine Zahl ist, und konvertiert ihn entsprechend in Integer oder Double.
     *
     * @param value Der Wert als String
     * @return Konvertierter Wert (Integer, Double oder originaler String)
     */
    private Object convertToNumberIfPossible(String value) {
        if (value == null) {
            return null;
        }
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value; // Falls keine Zahl, wird der originale String zurückgegeben
        }
    }
}

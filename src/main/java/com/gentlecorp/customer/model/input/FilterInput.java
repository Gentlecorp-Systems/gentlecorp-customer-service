package com.gentlecorp.customer.model.input;

import com.gentlecorp.customer.model.enums.FilterOptions;
import com.gentlecorp.customer.model.enums.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 2.2
 */
public record FilterInput(
    FilterOptions field,
    Operator operator,
    String value,
    List<FilterInput> AND,
    List<FilterInput> OR,
    List<FilterInput> NOR
) {
    private static final Logger log = LoggerFactory.getLogger(FilterInput.class);

    /**
     * Konvertiert das `FilterInput` in eine Map für die Verwendung mit MongoDB.
     *
     * @return Eine Map mit den umgewandelten Filterkriterien.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> query = new HashMap<>();

        if (field != null && operator != null && value != null) {
            // Adressfelder (z. B. address.city)
            if (field.name().startsWith("address_")) {
                String mongoField = "address." + field.name().substring(8); // Entfernt "address."
                // Adressfelder (z. B. address.city, address.street) mit Regex-Suche für Prefix-Matching
                if (operator == Operator.LIKE) {
                    query.put(mongoField, Map.of("$regex", value, "$options", "i")); // Case-insensitive
                } else if (operator == Operator.PREFIX) {
                    query.put(mongoField, Map.of("$regex", "^" + value, "$options", "i")); //Case-insensitive Prefix-Suche
                } else {
                query.put(mongoField, Map.of("$" + operator.name().toLowerCase(), value));
                }
            }

            // LIKE: String-Suche mit MongoDB Regex
            else if (operator == Operator.LIKE) {
                query.put(field.name(), Map.of("$regex", value, "$options", "i")); // Case-insensitive
            }
            else if (operator == Operator.PREFIX) {
                query.put(field.name(), Map.of("$regex", "^" + value, "$options", "i"));
            }

            // birthdate: Konvertiere `value` in MongoDB-kompatibles `Date`
            else if (field == FilterOptions.birthdate) {
                if (operator == Operator.IN) {
                    // IN: Filter für ein Datumsintervall (zwei Werte)
                    log.debug("IN-Filter für Geburtstage: " + value);
                    String[] dates = value.split(",");
                    if (dates.length == 2) {
                        Date startDate = convertToDate(dates[0]);
                        Date endDate = convertToDate(dates[1]);
                        if (startDate != null && endDate != null) {
                            query.put("$and", List.of(
                                Map.of(field.name(), Map.of("$gte", startDate)),
                                Map.of(field.name(), Map.of("$lte", endDate))
                            ));
                        }
                    }
                }
                else {
                    // GTE, LTE, EQ: Standard-Datumsfilter
                    Date date = convertToDate(value);
                    assert date != null;
                    query.put(field.name(), Map.of("$" + operator.name().toLowerCase(), date));
                }
            }
            // Boolean-Handling für `subscribed`
            else if (field == FilterOptions.subscribed) {
                Boolean booleanValue = parseBoolean(value);
                if (booleanValue != null) {
                    query.put(field.name(), booleanValue);
                }
            }
            // Standard-Felder
            else {
                query.put(field.name(), Map.of("$" + operator.name().toLowerCase(), convertToNumberIfPossible(value)));
            }
        }

        // Verarbeitung von AND, OR, NOR
        if (AND != null && !AND.isEmpty()) query.put("$and", AND.stream().map(FilterInput::toMap).toList());
        if (OR != null && !OR.isEmpty()) query.put("$or", OR.stream().map(FilterInput::toMap).toList());
        if (NOR != null && !NOR.isEmpty()) query.put("$nor", NOR.stream().map(FilterInput::toMap).toList());

        return query;
    }

    /**
     * Wandelt ein String-Datum im Format `yyyy-MM-dd` in ein MongoDB-kompatibles `Date`-Objekt um.
     */
    private Date convertToDate(String dateString) {
        try {
            LocalDate localDate = LocalDate.parse(dateString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant()); // 💡 Fix: Immer in UTC umwandeln
        } catch (Exception e) {
            System.err.println("Ungültiges Datumsformat: " + dateString);
            return null;
        }
    }


    /**
     * Wandelt einen String in einen Boolean um (`"true"` -> `true`, `"false"` -> `false`).
     */
    private Boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        return null;
    }

    /**
     * Prüft, ob der Wert eine Zahl ist, und konvertiert ihn entsprechend in Integer oder Double.
     *
     * @param value Der Wert als String
     * @return Konvertierter Wert (Integer, Double oder originaler String)
     */
    private Object convertToNumberIfPossible(String value) {
        if (value == null) return null;
        try {
            return value.contains(".") ? Double.parseDouble(value) : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value; // Falls keine Zahl, wird der originale String zurückgegeben
        }
    }
}

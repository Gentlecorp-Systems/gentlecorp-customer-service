package com.gentlecorp.customer.model.enums;

/**
 * Definiert alle möglichen Filteroptionen für Abfragen in der Kundenverwaltung.
 * <p>
 * Dieses Enum ermöglicht dynamische Suchanfragen auf Basis der hier definierten Felder.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public enum FilterOptions {
    id,
    version,
    lastName,
    firstName,
    email,
    phoneNumber,
    username,
    tierLevel,
    subscribed,
    birthdate,
    gender,
    maritalStatus,
    customerState,
    address,
    contactOptions,
    interests
}

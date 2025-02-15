package com.gentlecorp.customer.model.enums;

/**
 * Definiert alle möglichen Filteroptionen für Abfragen in der Kundenverwaltung.
 * <p>
 * Dieses Enum ermöglicht dynamische Suchanfragen auf Basis der hier definierten Felder.
 * </p>
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 2.0
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
    contactOptions,
    interests,
    address_street,
    address_houseNumber,
    address_zipCode,
    address_city,
    address_state,
    address_country
}

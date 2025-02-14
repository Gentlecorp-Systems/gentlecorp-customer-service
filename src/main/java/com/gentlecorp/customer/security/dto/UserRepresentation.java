package com.gentlecorp.customer.security.dto;

/**
 * Datentransferobjekt (DTO) für die Benutzerrepräsentation.
 * <p>
 * Dieses DTO wird verwendet, um Benutzerinformationen darzustellen,
 * die für die Benutzerverwaltung relevant sind.
 * </p>
 *
 * @param id        Die eindeutige ID des Benutzers.
 * @param username  Der Benutzername.
 * @param email     Die E-Mail-Adresse des Benutzers.
 * @param firstName Der Vorname des Benutzers.
 * @param lastName  Der Nachname des Benutzers.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record UserRepresentation(
    String id,
    String username,
    String email,
    String firstName,
    String lastName
) {
}

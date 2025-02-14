package com.gentlecorp.customer.security.dto;

/**
 * Datentransferobjekt (DTO) für Login-Anfragen.
 * <p>
 * Dieses DTO wird für die Authentifizierung eines Benutzers verwendet.
 * Es enthält den Benutzernamen und das Passwort.
 * </p>
 *
 * @param username Der Benutzername.
 * @param password Das Passwort.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record LoginDTO(
    String username,
    String password
) {
}

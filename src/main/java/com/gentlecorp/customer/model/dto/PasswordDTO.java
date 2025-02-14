package com.gentlecorp.customer.model.dto;

/**
 * Datentransferobjekt (DTO) für Passwortinformationen.
 * <p>
 * Dieses DTO wird verwendet, um ein Passwort sicher zu übermitteln,
 * beispielsweise bei der Erstellung eines neuen Kundenkontos oder beim Ändern eines Passworts.
 * </p>
 *
 * @param password Das Passwort des Benutzers.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record PasswordDTO(
    String password
) {
}

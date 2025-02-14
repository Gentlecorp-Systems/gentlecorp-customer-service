package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Diese Ausnahme wird ausgelöst, wenn ein ungültiges Passwort verwendet wird.
 * <p>
 * Sie signalisiert, dass das eingegebene Passwort nicht den Sicherheitsanforderungen entspricht.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Getter
public class PasswordInvalidException extends RuntimeException {

    /** Das ungültige Passwort. */
    private final String password;

    /**
     * Erstellt eine neue `PasswordInvalidException` mit dem ungültigen Passwort.
     *
     * @param password Das ungültige Passwort.
     */
    public PasswordInvalidException(final String password) {
        super("Ungültiges Passwort: " + password);
        this.password = password;
    }
}

/**
 * Dieses Paket enthält benutzerdefinierte Ausnahme-Klassen für das Fehlerhandling der Anwendung.
 * <p>
 * Die enthaltenen Ausnahmen decken verschiedene Fehlerzustände ab, darunter:
 * </p>
 *
 * <ul>
 *   <li>{@link com.gentlecorp.customer.exception.AccessForbiddenException} – Zugriff verweigert.</li>
 *   <li>{@link com.gentlecorp.customer.exception.CommonExceptionHandler} – Zentrale Fehlerbehandlung.</li>
 *   <li>{@link com.gentlecorp.customer.exception.NotFoundException} – Ressource nicht gefunden.</li>
 *   <li>{@link com.gentlecorp.customer.exception.EmailExistsException} – E-Mail existiert bereits.</li>
 *   <li>{@link com.gentlecorp.customer.exception.UsernameExistsException} – Benutzername existiert bereits.</li>
 *   <li>{@link com.gentlecorp.customer.exception.ContactExistsException} – Kontakt existiert bereits.</li>
 *   <li>{@link com.gentlecorp.customer.exception.VersionAheadException} – Version zu weit voraus.</li>
 *   <li>{@link com.gentlecorp.customer.exception.VersionInvalidException} – Ungültige Version.</li>
 *   <li>{@link com.gentlecorp.customer.exception.VersionOutdatedException} – Veraltete Version.</li>
 *   <li>{@link com.gentlecorp.customer.exception.PasswordInvalidException} – Ungültiges Passwort.</li>
 *   <li>{@link com.gentlecorp.customer.exception.SignUpException} – Fehler bei der Registrierung.</li>
 *   <li>{@link com.gentlecorp.customer.exception.ConstraintViolationsException} – Verletzung von Validierungsregeln.</li>
 *   <li>{@link com.gentlecorp.customer.exception.IllegalArgumentException} – Ungültiges Argument.</li>
 * </ul>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.1
 */
package com.gentlecorp.customer.exception;

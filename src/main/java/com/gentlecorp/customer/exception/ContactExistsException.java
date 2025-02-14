package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Diese Ausnahme wird ausgelöst, wenn ein Kontakt bereits existiert.
 * <p>
 * Die Ausnahme wird verwendet, um darauf hinzuweisen, dass ein bestimmter Kontakt bereits
 * in der Liste des Benutzers vorhanden ist und daher nicht erneut hinzugefügt werden kann.
 * </p>
 *
 * @since 13.02.2025
 * @version 1.0
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class ContactExistsException extends RuntimeException {
  /** Der Nachname des bereits existierenden Kontakts. */
  private final String lastName;

  /** Der Vorname des bereits existierenden Kontakts. */
  private final String firstName;

  /**
   * Erstellt eine neue `ContactExistsException`, wenn ein doppelter Kontakt gefunden wird.
   *
   * @param lastName  Der Nachname des Kontakts.
   * @param firstName Der Vorname des Kontakts.
   */
  public ContactExistsException(String lastName, String firstName) {
    super(String.format("Der Kontakt: %s %s ist bereits in deiner Liste", lastName, firstName));

    this.lastName = lastName;
    this.firstName = firstName;
  }
}

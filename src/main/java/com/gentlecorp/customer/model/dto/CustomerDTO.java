package com.gentlecorp.customer.model.dto;

import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.List;

/**
 * Datenübertragungsobjekt (DTO) für Kundendaten.
 * <p>
 * Dieses DTO stellt die Struktur eines Kunden dar und stellt sicher, dass alle Eingaben
 * bestimmten Validierungsregeln entsprechen.
 * </p>
 *
 * Validierte Eigenschaften:
 * <ul>
 *     <li>Nachname und Vorname müssen bestimmte Zeichen enthalten und eine maximale Länge haben.</li>
 *     <li>Die E-Mail-Adresse muss gültig sein.</li>
 *     <li>Der Benutzername hat spezifische Zeichenbeschränkungen.</li>
 *     <li>Das Geburtsdatum darf nicht in der Zukunft liegen.</li>
 *     <li>Interessen und Kontaktoptionen müssen eindeutig sein.</li>
 * </ul>
 *
 * @param lastName        Der Nachname des Kunden.
 * @param firstName       Der Vorname des Kunden.
 * @param email           Die E-Mail-Adresse des Kunden.
 * @param phoneNumber     Die Telefonnummer des Kunden.
 * @param username        Der Benutzername des Kunden.
 * @param tierLevel       Die Mitgliedschaftsstufe.
 * @param subscribed    Gibt an, ob der Kunde abonniert ist.
 * @param birthdate       Das Geburtsdatum des Kunden.
 * @param gender          Das Geschlecht des Kunden.
 * @param maritalStatus   Der Familienstand des Kunden.
 * @param interests       Die Interessen des Kunden.
 * @param contactOptions  Die bevorzugten Kontaktoptionen.
 * @param address         Die Adresse des Kunden.
 *
 * @since 17.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 2.0
 */
public record CustomerDTO(
    @NotNull(message = "Bitte gib deinen Nachnamen an.")
    @Pattern(regexp = LAST_NAME_PATTERN, message = "Der Nachname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.")
    @Size(max = NAME_MAX_LENGTH, message = "Der Nachname darf maximal {max} Zeichen lang sein.")
    String lastName,

    @NotNull(message = "Bitte gib deinen Vornamen an.")
    @Pattern(regexp = FIRST_NAME_PATTERN, message = "Der Vorname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.")
    @Size(max = NAME_MAX_LENGTH, message = "Der Vorname darf maximal {max} Zeichen lang sein.")
    String firstName,

    @NotNull(message = "Bitte gib deine E-Mail-Adresse an.")
    @Email(message = "Bitte gib eine gültige E-Mail-Adresse an.")
    @Size(max = EMAIL_MAX_LENGTH, message = "Die E-Mail darf maximal {max} Zeichen lang sein.")
    String email,

    @Pattern(regexp = PHONE_NUMBER_PATTERN, message = "Bitte gib eine gültige Telefonnummer an. Die Telefonnummer muss zwischen 7 und 25 Zeichen lang sein.")
    //@Size(min = PHONE_NUMBER_MIN_LENGTH, max = PHONE_NUMBER_MAX_LENGTH, message = "Die Telefonnummer muss zwischen {min} und {max} Zeichen lang sein.")
    String phoneNumber,

    @NotNull(message = "Bitte gib einen Benutzernamen an.")
    @Pattern(regexp = USERNAME_PATTERN, message = "Der Benutzername muss zwischen 4 und 20 Zeichen lang sein. Der Benutzername darf nur Buchstaben, Zahlen, Unterstriche, Punkte oder Bindestriche enthalten.")
    //@Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "Der Benutzername muss zwischen {min} und {max} Zeichen lang sein.")
    String username,

    @Min(value = MIN_LEVEL, message = "Die Mitgliedschaftsstufe muss mindestens {value} sein.")
    @Max(value = MAX_LEVEL, message = "Die Mitgliedschaftsstufe darf maximal {value} sein.")
    int tierLevel,

    boolean subscribed,

    @Past(message = "Das Geburtsdatum muss in der Vergangenheit liegen.")
    @NotNull(message = "Das Geburtsdatum ist erforderlich.")
    LocalDate birthdate,

    @NotNull(message = "Bitte gib dein Geschlecht an.")
    GenderType gender,

    @NotNull(message = "Bitte gib deinen Familienstand an.")
    MaritalStatusType maritalStatus,

    @UniqueElements(message = "Die Interessen müssen eindeutig sein.")
    List<InterestType> interests,

    @NotNull(message = "Bitte gib mindestens eine bevorzugte Kontaktoption an.")
    @UniqueElements(message = "Die Kontaktoptionen müssen eindeutig sein.")
    List<ContactOptionsType> contactOptions,

    @NotNull(groups = OnCreate.class, message = "Bitte gib deine Adresse an.")
    @Valid
    AddressDTO address
) {
  public interface OnCreate { }

  // Konstante für die minimale und maximale Mitgliedschaftsstufe.
  public static final long MIN_LEVEL = 1L;
  public static final long MAX_LEVEL = 3L;

  // Muster für gültige Namen (Nachname & Vorname).
  public static final String LAST_NAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
  public static final String FIRST_NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

  // Muster für gültige Benutzernamen.
  public static final String USERNAME_PATTERN = "[a-zA-Z0-9_\\-.]{4,}";

  // Zeichenbeschränkungen für verschiedene Eingaben.
  public static final int USERNAME_MAX_LENGTH = 20;
  public static final int USERNAME_MIN_LENGTH = 4;
  public static final int NAME_MAX_LENGTH = 40;
  private static final int EMAIL_MAX_LENGTH = 40;

  // Muster und Längenbeschränkungen für Telefonnummern.
  public static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9. ()-]{7,25}$";
  public static final int PHONE_NUMBER_MAX_LENGTH = 25;
  public static final int PHONE_NUMBER_MIN_LENGTH = 7;
}

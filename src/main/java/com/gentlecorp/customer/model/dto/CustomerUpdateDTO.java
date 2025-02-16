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
 * Datentransferobjekt (DTO) für Kundendaten.
 * <p>
 * Validiert Namen, E-Mail, Telefonnummer, Benutzername, Geburtsdatum, Interessen und Kontaktoptionen.
 * </p>
 *
 * @param lastName        Der Nachname des Kunden.
 * @param firstName       Der Vorname des Kunden.
 * @param email           Die E-Mail-Adresse des Kunden.
 * @param phoneNumber     Die Telefonnummer des Kunden.
 * @param username        Der Benutzername des Kunden.
 * @param tierLevel       Die Mitgliedschaftsstufe.
 * @param isSubscribed    Gibt an, ob der Kunde abonniert ist.
 * @param birthdate       Das Geburtsdatum des Kunden.
 * @param gender          Das Geschlecht des Kunden.
 * @param maritalStatus   Der Familienstand des Kunden.
 * @param interests       Die Interessen des Kunden.
 * @param contactOptions  Die bevorzugten Kontaktoptionen.
 * @param address         Die Adresse des Kunden.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record CustomerUpdateDTO(
  @Pattern(regexp = LAST_NAME_PATTERN, message = "Invalid last name format.")
  @Size(max = NAME_MAX_LENGTH, message = "Last name must not exceed {max} characters.")
  String lastName,

  @Pattern(regexp = FIRST_NAME_PATTERN, message = "First name should only contain letters.")
  @Size(max = NAME_MAX_LENGTH, message = "First name must not exceed {max} characters.")
  String firstName,

  @Email(message = "Please provide a valid email address.")
  @Size(max = EMAIL_MAX_LENGTH, message = "Email must not exceed {max} characters.")
  String email,

  @Pattern(regexp = PHONE_NUMBER_PATTERN, message = "Please provide a valid phone number.")
  @Size(min = PHONE_NUMBER_MIN_LENGTH, max = PHONE_NUMBER_MAX_LENGTH, message = "Phone number must be between {min} and {max} characters long.")
  String phoneNumber,

  @Pattern(regexp = USERNAME_PATTERN, message = "Username can only contain alphanumeric characters, underscores, dots, or hyphens.")
  @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "Username must be between {min} and {max} characters long.")
  String username,

  @Min(value = MIN_LEVEL, message = "Tier level must be at least {value}.")
  @Max(value = MAX_LEVEL, message = "Tier level must be at most {value}.")
  int tierLevel,

  boolean isSubscribed,

  @Past(message = "Birthdate must be a past date.")
  LocalDate birthdate,

  GenderType gender,

  MaritalStatusType maritalStatus,

  @UniqueElements(message = "Interests must be unique.")
  List<InterestType> interests,

  @UniqueElements(message = "Contact options must be unique.")
  List<ContactOptionsType> contactOptions,

    @Valid
  AddressDTO address
) {
  public interface OnCreate { }

  public static final long MIN_LEVEL = 1L;
  public static final long MAX_LEVEL = 3L;
  public static final String LAST_NAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
  public static final String USERNAME_PATTERN = "[a-zA-Z0-9_\\-.]{4,}";
  public static final int USERNAME_MAX_LENGTH = 20;
  public static final int USERNAME_MIN_LENGTH = 4;
  private static final int EMAIL_MAX_LENGTH = 40;
  public static final String FIRST_NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
  public static final int NAME_MAX_LENGTH = 40;
  public static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9. ()-]{7,25}$";
  public static final int PHONE_NUMBER_MAX_LENGTH = 25;
  public static final int PHONE_NUMBER_MIN_LENGTH = 7;
}

package com.gentlecorp.customer.model.entity;

import com.gentlecorp.customer.model.enums.*;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repräsentiert einen Kunden im System.
 * <p>
 * Diese Entität wird in der MongoDB in der Collection 'Customer' gespeichert.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Document(collection = "Customer")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /**
     * Eindeutige ID des Kunden.
     * Automatische UUID-Generierung bei der Erstellung.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Versionsnummer für die Optimistic Locking-Strategie.
     */
    @Version
    private int version;

    /**
     * Nachname des Kunden (Pflichtfeld).
     */
    @NotBlank(message = "Nachname darf nicht leer sein")
    private String lastName;

    /**
     * Vorname des Kunden (Pflichtfeld).
     */
    @NotBlank(message = "Vorname darf nicht leer sein")
    private String firstName;

    /**
     * E-Mail-Adresse des Kunden (einzigartig und validiert).
     */
    @Email(message = "Bitte eine gültige E-Mail-Adresse angeben")
    @Indexed(unique = true)
    private String email;

    /**
     * Telefonnummer des Kunden (Pflichtfeld).
     */
    @NotBlank(message = "Telefonnummer darf nicht leer sein")
    private String phoneNumber;

    /**
     * Benutzername des Kunden (einzigartig und validiert).
     */
    @NotBlank(message = "Benutzername darf nicht leer sein")
    @Indexed(unique = true)
    private String username;

    /**
     * Stufe des Kunden im System (1–10).
     */
    @Min(value = 1, message = "Tier Level muss mindestens 1 sein")
    @Max(value = 10, message = "Tier Level darf maximal 10 sein")
    private int tierLevel;

    /**
     * Gibt an, ob der Kunde abonniert ist.
     */
    private boolean subscribed;

    /**
     * Geburtsdatum des Kunden (muss in der Vergangenheit liegen).
     */
    @Past(message = "Geburtsdatum muss in der Vergangenheit liegen")
    private LocalDate birthdate;

    /**
     * Geschlecht des Kunden.
     */
    private GenderType gender;

    /**
     * Familienstand des Kunden.
     */
    private MaritalStatusType maritalStatus;

    /**
     * Status des Kunden (z. B. aktiv, inaktiv).
     */
    private StatusType customerState;

    /**
     * Adresse des Kunden (Pflichtfeld).
     */
    @NotNull(message = "Adresse darf nicht null sein")
    private Address address;

    /**
     * Liste von Kontakten, die mit dem Kunden verknüpft sind.
     */
    private List<Contact> contacts;

    /**
     * Zeitstempel der Erstellung des Kunden-Dokuments.
     */
    @CreatedDate
    private LocalDateTime created;

    /**
     * Zeitstempel der letzten Änderung des Kunden-Dokuments.
     */
    @LastModifiedDate
    private LocalDateTime updated;

    /**
     * Liste der Interessen des Kunden.
     */
    private List<InterestType> interests;

    /**
     * Bevorzugte Kontaktoptionen des Kunden.
     */
    private List<ContactOptionsType> contactOptions;
}

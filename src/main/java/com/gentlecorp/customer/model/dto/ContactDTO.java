package com.gentlecorp.customer.model.dto;

import com.gentlecorp.customer.model.annotation.ValidDateRange;
import com.gentlecorp.customer.model.enums.RelationshipType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;

import static com.gentlecorp.customer.model.dto.CustomerDTO.FIRST_NAME_PATTERN;
import static com.gentlecorp.customer.model.dto.CustomerDTO.LAST_NAME_PATTERN;
import static com.gentlecorp.customer.model.dto.CustomerDTO.NAME_MAX_LENGTH;

/**
 * Datentransferobjekt (DTO) für Kontaktinformationen.
 * <p>
 * Validiert Namen, Beziehungstyp, Abhebelimit sowie Gültigkeit von Start- und Enddatum.
 * </p>
 *
 * @param lastName          Der Nachname des Kontakts.
 * @param firstName         Der Vorname des Kontakts.
 * @param relationship      Die Art der Beziehung.
 * @param withdrawalLimit   Das Abhebelimit für den Kontakt.
 * @param isEmergencyContact Gibt an, ob der Kontakt ein Notfallkontakt ist.
 * @param startDate         Das Startdatum der Beziehung.
 * @param endDate           Das Enddatum der Beziehung.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@ValidDateRange
public record ContactDTO(
    @NotNull(message = "Bitte gib deinen Nachnamen an.")
    @Pattern(regexp = LAST_NAME_PATTERN, message = "Der Nachname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.")
    @Size(max = NAME_MAX_LENGTH, message = "Der Nachname darf maximal {max} Zeichen lang sein.")
    String lastName,

    @NotNull(message = "Bitte gib deinen Vornamen an.")
    @Pattern(regexp = FIRST_NAME_PATTERN, message = "Der Vorname darf nur Buchstaben enthalten und sollte mit einem großen Buchstaben anfangen.")
    @Size(max = NAME_MAX_LENGTH, message = "Der Vorname darf maximal {max} Zeichen lang sein.")
    String firstName,

    @NotNull(message = "Der Beziehungstyp ist erforderlich.")
    RelationshipType relationship,

    @Min(value = 0, message = "Das Auszahlungslimit darf nicht negativ sein")
    int withdrawalLimit,

    boolean isEmergencyContact,

    @FutureOrPresent(message = "Das Startdatum darf nicht in der Vergangenheit liegen.")
    LocalDate startDate,

    @FutureOrPresent(message = "Das Enddatum darf nicht in der Vergangenheit liegen.")
    LocalDate endDate
) {
}

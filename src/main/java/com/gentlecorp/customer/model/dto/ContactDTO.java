package com.gentlecorp.customer.model.dto;

import com.gentlecorp.customer.model.annotation.ValidDateRange;
import com.gentlecorp.customer.model.enums.RelationshipType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Bitte geben Sie einen Nachnamen an.")
    @Pattern(regexp = LAST_NAME_PATTERN, message = "Ungültiges Format für den Nachnamen.")
    @Size(max = NAME_MAX_LENGTH, message = "Der Nachname darf maximal " + NAME_MAX_LENGTH + " Zeichen enthalten.")
    String lastName,

    @NotNull(message = "Bitte geben Sie einen Vornamen an.")
    @Pattern(regexp = FIRST_NAME_PATTERN, message = "Ungültiges Format für den Vornamen.")
    @Size(max = NAME_MAX_LENGTH, message = "Der Vorname darf maximal " + NAME_MAX_LENGTH + " Zeichen enthalten.")
    String firstName,

    @NotNull(message = "Der Beziehungstyp ist erforderlich.")
    RelationshipType relationship,

    int withdrawalLimit,

    boolean isEmergencyContact,

    @FutureOrPresent(message = "Das Startdatum darf nicht in der Vergangenheit liegen.")
    LocalDate startDate,

    @FutureOrPresent(message = "Das Enddatum darf nicht in der Vergangenheit liegen.")
    LocalDate endDate
) {
}

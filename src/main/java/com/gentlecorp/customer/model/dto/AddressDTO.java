package com.gentlecorp.customer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Datentransferobjekt (DTO) für Adressinformationen.
 * <p>
 * Enthält Validierungsregeln für Straßenname, Hausnummer, PLZ, Stadt, Bundesland und Land.
 * </p>
 *
 * @param street       Der Straßenname.
 * @param houseNumber  Die Hausnummer.
 * @param zipCode      Die Postleitzahl.
 * @param city         Die Stadt.
 * @param state        Das Bundesland.
 * @param country      Das Land.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record AddressDTO(
    @Pattern(message = "Bitte einen gültigen Straßennamen eingeben.", regexp = STREET_PATTERN)
    @NotNull(message = "Der Straßenname darf nicht null sein.")
    String street,

    @NotNull(message = "Die Hausnummer darf nicht null sein.")
    String houseNumber,

    @NotNull(message = "Die Postleitzahl darf nicht null sein.")
    String zipCode,

    @NotNull(message = "Der Stadtname darf nicht null sein.")
    String city,

    @NotNull(message = "Das Bundesland darf nicht null sein.")
    String state,

    @NotBlank(message = "Der Ländername darf nicht leer sein.")
    String country
) {
  public static final String STREET_PATTERN = "^[a-zA-ZäöüßÄÖÜ\\s]+(?:\\s\\d+)?$";
}

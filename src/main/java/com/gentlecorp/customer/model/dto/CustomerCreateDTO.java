package com.gentlecorp.customer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Datentransferobjekt (DTO) für die Erstellung eines neuen Kunden.
 * <p>
 * Enthält sowohl Kundeninformationen als auch das zugehörige Passwort.
 * </p>
 *
 * @param customerDTO Die Kundendaten.
 * @param passwordDTO Die zugehörigen Passwortinformationen.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record CustomerCreateDTO(
    @JsonProperty("customer")
    CustomerDTO customerDTO,

    @JsonProperty("password")
    PasswordDTO passwordDTO
) {
}

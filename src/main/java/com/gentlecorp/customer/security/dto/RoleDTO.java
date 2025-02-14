package com.gentlecorp.customer.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Datentransferobjekt (DTO) zur Darstellung einer Benutzerrolle.
 * <p>
 * Wird für die Verwaltung von Benutzerrollen verwendet.
 * </p>
 *
 * @param id   Die eindeutige ID der Rolle.
 * @param name Der Name der Rolle.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record RoleDTO(
    @JsonProperty("id")
    String id,

    @JsonProperty("name")
    String name
) {
}

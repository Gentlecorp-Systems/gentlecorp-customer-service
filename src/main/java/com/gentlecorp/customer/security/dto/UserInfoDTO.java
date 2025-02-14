package com.gentlecorp.customer.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Datentransferobjekt (DTO) zur Darstellung von Benutzerinformationen.
 * <p>
 * Wird zur Speicherung von Benutzeridentifikationen verwendet.
 * </p>
 *
 * @param sub Die eindeutige Benutzer-ID.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record UserInfoDTO(
    @JsonProperty("sub")
    String sub
) {
}

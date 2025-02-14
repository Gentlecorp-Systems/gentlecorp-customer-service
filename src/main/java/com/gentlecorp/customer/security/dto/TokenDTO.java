package com.gentlecorp.customer.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gentlecorp.customer.security.enums.ScopeType;
import com.gentlecorp.customer.security.enums.TokenType;

/**
 * Datentransferobjekt (DTO) für Authentifizierungs-Token.
 * <p>
 * Enthält Informationen über das Token, die Ablaufzeiten und den Token-Typ.
 * </p>
 *
 * @param access_token       Das Zugriffstoken.
 * @param expires_in         Ablaufzeit des Zugriffstokens in Sekunden.
 * @param refresh_expires_in Ablaufzeit des Refresh-Tokens in Sekunden.
 * @param refresh_token      Das Refresh-Token.
 * @param token_type         Der Typ des Tokens (z. B. Bearer).
 * @param notBeforePolicy    Zeitpunkt, bevor der Token gültig ist.
 * @param session_state      Der Sitzungsstatus.
 * @param id_token           Das ID-Token.
 * @param scope              Der Gültigkeitsbereich des Tokens.
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public record TokenDTO(
  String access_token,

  int expires_in,

  int refresh_expires_in,

  String refresh_token,

  TokenType token_type,

  @JsonProperty("not-before-policy")
  int notBeforePolicy,

  String session_state,

  String id_token,

  ScopeType scope
) {
}

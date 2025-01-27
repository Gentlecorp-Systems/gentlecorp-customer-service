package com.gentlecorp.customer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gentlecorp.customer.model.enums.ScopeType;
import com.gentlecorp.customer.model.enums.TokenType;

public record TokenDTO(
  @JsonProperty("access_token")
  String accessToken,

  @JsonProperty("expires_in")
  int expiresIn,

  @JsonProperty("refresh_expires_in")
  int refreshExpiresIn,

  @JsonProperty("refresh_token")
  String refreshToken,

  @JsonProperty("token_type")
  TokenType tokenType,

  @JsonProperty("not-before-policy")
  int notBeforePolicy,

  @JsonProperty("session_state")
  String sessionState,

  @JsonProperty("id_token")
  String idToken,

  @JsonProperty("scope")
  ScopeType scope
) {
}

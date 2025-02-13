package com.gentlecorp.customer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gentlecorp.customer.model.enums.ScopeType;
import com.gentlecorp.customer.model.enums.TokenType;

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

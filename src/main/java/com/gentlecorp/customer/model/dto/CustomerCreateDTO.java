package com.gentlecorp.customer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreateDTO(
  @JsonProperty("customer")
  CustomerDTO customerDTO,

  @JsonProperty("password")
  PasswordDTO passwordDTO
) {
}

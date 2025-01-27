package com.gentlecorp.customer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressDTO(
  @Pattern(message = "Please enter a valid street name, containing only letters.", regexp = STREET_PATTERN)
  @NotNull(message = "Street name cannot be null.")
  String street,

  @NotNull(message = "House number cannot be null.")
  String houseNumber,

  @NotNull(message = "Zip code cannot be null.")
  String zipCode,

  @NotNull(message = "City name cannot be null.")
  String city,

  @NotNull(message = "State name cannot be null.")
  String state,

  @NotBlank(message = "Country name cannot be empty.")
  String country
) {
  public static final String STREET_PATTERN = "^[a-zA-ZäöüßÄÖÜ\\s]+(?:\\s\\d+)?$";
}

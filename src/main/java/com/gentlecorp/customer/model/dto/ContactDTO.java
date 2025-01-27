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


@ValidDateRange// Custom annotation to validate startDate and endDate relationship
public record ContactDTO(
  @NotNull(message = "Please provide a last name.")
  @Pattern(regexp = LAST_NAME_PATTERN, message = "Last name format is invalid.")
  @Size(max = NAME_MAX_LENGTH, message = "Last name cannot exceed " + NAME_MAX_LENGTH + " characters.")
  String lastName,

  @NotNull(message = "Please provide a first name.")
  @Pattern(regexp = FIRST_NAME_PATTERN, message = "First name format is invalid.")
  @Size(max = NAME_MAX_LENGTH, message = "First name cannot exceed " + NAME_MAX_LENGTH + " characters.")
  String firstName,

  @NotNull(message = "Relationship type is required.")
  RelationshipType relationship,

  int withdrawalLimit,

  boolean isEmergencyContact,

  @FutureOrPresent(message = "Start date cannot be in the past.")
  LocalDate startDate,

  @FutureOrPresent(message = "End date cannot be in the past.")
  LocalDate endDate
) {
//  public interface OnCreate { }
}

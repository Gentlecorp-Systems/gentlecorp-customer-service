package com.gentlecorp.customer.util;

import com.gentlecorp.customer.model.annotation.ValidDateRange;
import com.gentlecorp.customer.model.dto.ContactDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, ContactDTO> {

  @Override
  public boolean isValid(ContactDTO contactDTO, ConstraintValidatorContext context) {
    if (contactDTO == null) {
      return true;
    }

    LocalDate startDate = contactDTO.startDate();
    LocalDate endDate = contactDTO.endDate();

    if (startDate != null && endDate != null) {
      return !startDate.isAfter(endDate);
    }

    return true;
  }
}

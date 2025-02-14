package com.gentlecorp.customer.util;

import com.gentlecorp.customer.model.annotation.ValidDateRange;
import com.gentlecorp.customer.model.dto.ContactDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Validiert, ob das Startdatum eines Kontakts vor dem Enddatum liegt.
 * <p>
 * Diese Klasse implementiert die `ConstraintValidator`-Schnittstelle und prüft,
 * ob das in `ContactDTO` definierte Startdatum nicht nach dem Enddatum liegt.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
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

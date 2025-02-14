package com.gentlecorp.customer.model.annotation;

import com.gentlecorp.customer.util.DateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation wird verwendet, um sicherzustellen, dass ein Datumsbereich gültig ist.
 * <p>
 * Die Validierung stellt sicher, dass das Startdatum vor oder gleich dem Enddatum liegt.
 * Die Prüfung wird durch den {@link DateRangeValidator} durchgeführt.
 * </p>
 *
 * <p>Beispiel für die Verwendung:</p>
 * <pre>
 * {@code
 * @ValidDateRange
 * public class Event {
 *     private LocalDate startDate;
 *     private LocalDate endDate;
 * }
 * }
 * </pre>
 *
 * @since 13.02.2025
 * @version 1.0
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 */
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

  /**
   * Standardfehlermeldung, falls die Validierung fehlschlägt.
   *
   * @return Die Fehlermeldung.
   */
  String message() default "Das Startdatum muss vor oder gleich dem Enddatum sein.";

  /**
   * Gruppen für die Validierung.
   *
   * @return Ein Array von Gruppenklassen.
   */
  Class<?>[] groups() default {};

  /**
   * Nutzlast für erweiterte Metadaten.
   *
   * @return Ein Array von {@link Payload}-Klassen.
   */
  Class<? extends Payload>[] payload() default {};
}

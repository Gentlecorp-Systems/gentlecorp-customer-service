package com.gentlecorp.customer.model.entity;

import com.gentlecorp.customer.model.enums.RelationshipType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Repräsentiert einen Kontakt eines Kunden.
 * Wird als eingebettetes Attribut in der Customer-Collection verwendet.
 * Der Kontakt verweist auf einen anderen Kunden über die ID.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

  /**
   * Die ID des zugehörigen Kunden (Pflichtfeld).
   */
  @NotNull(message = "Die Kontakt-ID darf nicht null sein")
  private UUID customerId;

  /**
   * Beziehung des Kontakts zum Kunden (Pflichtfeld).
   */
  @NotNull(message = "Die Beziehung darf nicht null sein")
  private RelationshipType relationship;

  /**
   * Auszahlungslimit, das dieser Kontakt für den Kunden hat (mindestens 0).
   */
  @Min(value = 0, message = "Das Auszahlungslimit darf nicht negativ sein")
  private int withdrawalLimit;

  /**
   * Gibt an, ob der Kontakt ein Notfallkontakt ist.
   */
  private boolean emergencyContact;

  /**
   * Startdatum der Beziehung.
   */
  private String startDate;

  /**
   * Enddatum der Beziehung.
   */
  private String endDate;
}

package com.gentlecorp.customer.model.entity;

import com.gentlecorp.customer.model.enums.RelationshipType;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repräsentiert einen Kontakt eines Kunden.
 * <p>
 * Kontakte verweisen auf andere Kunden über deren ID und definieren Beziehungen zwischen ihnen.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Document(collection = "Contact")
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
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Versionsnummer für die Optimistic Locking-Strategie.
   */
  @Version
  private int version;

  private String lastName;
  private String firstName;

  /**
   * Beziehung des Kontakts zum Kunden (Pflichtfeld).
   */
  @NotNull(message = "Die Beziehung darf nicht null sein")
  private RelationshipType relationship;

  /**
   * Auszahlungslimit, das dieser Kontakt für den Kunden hat (mindestens 0).
   */

  private int withdrawalLimit;

  /**
   * Gibt an, ob der Kontakt ein Notfallkontakt ist.
   */
  private boolean emergencyContact;

  /**
   * Startdatum der Beziehung.
   */
  private LocalDate startDate;

  /**
   * Enddatum der Beziehung.
   */
  private LocalDate endDate;

  @CreationTimestamp
  private LocalDateTime created;

  @UpdateTimestamp
  private LocalDateTime updated;

  public void set(final Contact contact) {
    lastName = contact.getLastName() != null ? contact.getLastName() : lastName;
    firstName = contact.getFirstName() != null ? contact.getFirstName() : firstName;
    relationship = contact.getRelationship() != null ? contact.getRelationship() : relationship;
    withdrawalLimit = contact.getWithdrawalLimit() == 0 ? contact.withdrawalLimit : withdrawalLimit;
    emergencyContact = contact.emergencyContact || emergencyContact;
    startDate = contact.getStartDate() != null ? contact.getStartDate() : startDate;
    endDate = contact.getEndDate() != null ? contact.getEndDate() : endDate;
  }
}

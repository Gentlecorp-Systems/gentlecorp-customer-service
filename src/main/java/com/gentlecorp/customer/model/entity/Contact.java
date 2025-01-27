package com.gentlecorp.customer.model.entity;

import com.gentlecorp.customer.model.enums.RelationshipType;
import com.gentlecorp.customer.model.interfaces.VersionedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contact")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Contact implements VersionedEntity {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Version
  private int version;
  private String lastName;
  private String firstName;

  @Enumerated(EnumType.STRING)
  private RelationshipType relationship;
  private int withdrawalLimit;
  private boolean isEmergencyContact;
  private LocalDate startDate;
  private LocalDate endDate;

  @CreationTimestamp
  private LocalDateTime created;

  @UpdateTimestamp
  private LocalDateTime updated;

  public void set(final Contact contact) {
    lastName = contact.getLastName();
    firstName = contact.getFirstName();
    relationship = contact.getRelationship();
    withdrawalLimit = contact.getWithdrawalLimit();
    isEmergencyContact = contact.isEmergencyContact();
    startDate = contact.getStartDate();
    endDate = contact.getEndDate();
  }
}

package com.gentlecorp.customer.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.enums.RelationshipType;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.UUID;

@JsonPropertyOrder({
  "customerId", "relationship", "withdrawalLimit", "emergencyContact", "startDate", "endDate"
})
@Relation(collectionRelation = "addresses", itemRelation = "address")
@Getter
@ToString(callSuper = true)
public class ContactModel extends RepresentationModel<ContactModel> {
  private final UUID customerId;
  private final RelationshipType relationship;
  private final int withdrawalLimit;
  private final boolean isEmergencyContact;
  private final LocalDate startDate;
  private final LocalDate endDate;

  public ContactModel(final Contact contact) {
    this.customerId = contact.getCustomerId();
    this.relationship = contact.getRelationship();
    this.withdrawalLimit = contact.getWithdrawalLimit();
    this.isEmergencyContact = contact.isEmergencyContact();
    this.startDate = contact.getStartDate();
    this.endDate = contact.getEndDate();
  }
}

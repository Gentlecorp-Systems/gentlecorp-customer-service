package com.gentlecorp.customer.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.customer.model.entity.Customer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@JsonPropertyOrder({
  "username", "lastName", "firstName", "email","phoneNumber", "subscribed", "tierLevel",
  "birthdate","customerState", "gender", "maritalStatus", "address", "contactOptionsType", "interests",
  "contacts"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class FullCustomerModel extends BaseCustomerModel<FullCustomerModel> {
  private final List<ContactModel> contacts;

  public FullCustomerModel(Customer customer) {
    super(customer);
    this.contacts = customer.getContacts().stream().map(ContactModel::new).toList();
  }
}

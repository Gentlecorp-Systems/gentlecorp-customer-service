package com.gentlecorp.customer.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.customer.model.entity.Address;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@JsonPropertyOrder({
  "street", "houseNumber", "zipCode", "city", "state", "country"
})
@Relation(collectionRelation = "addresses", itemRelation = "address")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class AddressModel extends RepresentationModel<AddressModel> {
  private final String street;
  private final String houseNumber;
  private final String zipCode;
  private final String city;
  private final String state;
  private final String country;


  public AddressModel(final Address address) {
    this.street = address.getStreet();
    this.houseNumber = address.getHouseNumber();
    this.zipCode = address.getZipCode();
    this.city = address.getCity();
    this.state = address.getState();
    this.country = address.getCountry();
  }
}

package com.gentlecorp.customer.model;

import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.enums.StatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract class BaseCustomerModel<T extends BaseCustomerModel<T>> extends RepresentationModel<T> {
  private final String lastName;
  private final String firstName;
  private final String email;
  private final String phoneNumber;
  private final String username;
  private final int tierLevel;
  private final boolean isSubscribed;
  private final LocalDate birthdate;
  private final StatusType customerState;
  private final GenderType gender;
  private final MaritalStatusType maritalStatus;
  private final AddressModel address;
  private final List<ContactOptionsType> contactOptions;
  private final List<InterestType> interests;

  public BaseCustomerModel(Customer customer) {
    this.lastName = customer.getLastName();
    this.firstName = customer.getFirstName();
    this.email = customer.getEmail();
    this.phoneNumber = customer.getPhoneNumber();
    this.username = customer.getUsername();
    this.tierLevel = customer.getTierLevel();
    this.isSubscribed = customer.isSubscribed();
    this.birthdate = customer.getBirthdate();
    this.gender = customer.getGender();
    this.maritalStatus = customer.getMaritalStatus();
    this.customerState = customer.getCustomerState();
    this.address = new AddressModel(customer.getAddress());
    this.contactOptions = customer.getContactOptions();
    this.interests = customer.getInterests();
  }
}

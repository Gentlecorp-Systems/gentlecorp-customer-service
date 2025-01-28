package com.gentlecorp.customer.model;

import com.gentlecorp.customer.model.dto.AddressDTO;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.enums.StatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TestCustomer(
  UUID id,
  int version,
  String lastName,
  String firstName,
  String email,
  String phoneNumber,
  String username,
  int tierLevel,
  boolean subscribed,
  LocalDate birthdate,
  GenderType gender,
  MaritalStatusType maritalStatus,
  StatusType customerState,
  AddressDTO address,
  List<ContactDTO> contacts,
  List<InterestType> interests,
  List<ContactOptionsType> contactOptions,
  LocalDateTime created,
  LocalDateTime updated,
  HateoasLinks _links
) {
}

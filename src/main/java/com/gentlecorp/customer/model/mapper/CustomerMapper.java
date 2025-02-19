package com.gentlecorp.customer.model.mapper;

import com.gentlecorp.customer.model.dto.AddressDTO;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import com.gentlecorp.customer.model.dto.CustomerUpdateDTO;
import com.gentlecorp.customer.model.entity.Address;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

  Customer toCustomer(CustomerDTO customerDTO);
  Customer toCustomer(CustomerUpdateDTO customerDTO);
  Address toAddress(AddressDTO addressDTO);
  Contact toContact(ContactDTO contactDTO);

}

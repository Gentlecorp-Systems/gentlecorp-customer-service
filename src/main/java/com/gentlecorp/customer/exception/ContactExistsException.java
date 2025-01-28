package com.gentlecorp.customer.exception;

import lombok.Getter;

@Getter
public class ContactExistsException extends RuntimeException {
  private final String lastName;
  private final String firstName;

  public ContactExistsException(String lastName, String firstName) {
    super(String.format("Der Kontakt: %s %s ist bereits in deiner Liste", lastName, firstName));

    this.lastName = lastName;
    this.firstName = firstName;
  }
}

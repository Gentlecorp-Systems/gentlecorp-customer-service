package com.gentlecorp.customer.exception;

import lombok.Getter;

@Getter
public class VersionAheadException extends RuntimeException {

  private final int version;

  public VersionAheadException(int version) {
    super(String.format("Provided version %d is ahead of the current version and is not yet applicable.", version));
    this.version = version;
  }

}

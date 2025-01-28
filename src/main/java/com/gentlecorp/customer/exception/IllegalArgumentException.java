package com.gentlecorp.customer.exception;

import lombok.Getter;

@Getter
public final class IllegalArgumentException extends RuntimeException {
    private final String key;
    private final int tier;

    public IllegalArgumentException(final String key) {
      super(String.format("Invalid key: %s", key));
        this.key = key;
        this.tier = 0;
    }

  public IllegalArgumentException(final int tier) {
    super(String.format("Invalid tier level: %s", tier));
    this.key = null;
    this.tier = 0;
  }
}

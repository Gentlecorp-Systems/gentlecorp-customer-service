package com.gentlecorp.customer.exception;

import lombok.Getter;

@Getter
public class PasswordInvalidException extends RuntimeException {
    private final String password;

    public PasswordInvalidException(final String password) {
        super("Ungueltiges Passwort " + password);
        this.password = password;
    }
}

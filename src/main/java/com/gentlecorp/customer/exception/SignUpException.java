package com.gentlecorp.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown during sign-up failures.
 * <p>
 * This exception is used to indicate that an error occurred during the sign-up process,
 * such as when required data is invalid or not provided. It results in an HTTP 500 (Internal Server Error) response.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SignUpException extends RuntimeException {

  /**
   * Constructs a new {@code SignUpException} with the specified detail message.
   *
   * @param message The detail message that explains the reason for the exception.
   *                This message is saved for later retrieval by the {@link #getMessage()} method.
   */
  public SignUpException(final String message) {
    super(message);
  }
}

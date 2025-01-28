package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Exception thrown when an attempt is made to create a user with a username that already exists.
 * <p>
 * This exception is used to indicate that the username provided during user registration
 * or update is already taken by another user. This prevents duplicate usernames in the system.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class UsernameExistsException extends RuntimeException {
  /**
   * The username that already exists.
   */
  private final String username;

  /**
   * Constructs a new {@code UsernameExistsException} with the specified username.
   *
   * @param username The username that already exists.
   */
  public UsernameExistsException(final String username) {
    super("Der Benutzername " + username + " existiert bereits.");
    this.username = username;
  }
}

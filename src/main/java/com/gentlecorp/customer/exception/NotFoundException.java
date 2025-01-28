package com.gentlecorp.customer.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Custom runtime exception thrown when a customer cannot be found.
 * <p>
 * This exception is used to indicate that no customer was found based on the provided ID or search criteria.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public final class NotFoundException extends RuntimeException {

  /**
   * The ID of the customer that was not found.
   * <p>
   * This field is used when a specific customer ID is not found in the system.
   * </p>
   */
  private final UUID id;

  /**
   * The search criteria that did not yield any results.
   * <p>
   * This field is used when no customers are found based on the provided search criteria.
   * </p>
   */
  private final Map<String, List<String>> searchCriteria;

  private final String message;

  /**
   * Constructs a new {@code NotFoundException} with a message indicating the specified customer ID was not found.
   *
   * @param id The ID of the customer that could not be found.
   */
  public NotFoundException(final UUID id) {
    super(String.format("No customer found with ID: %s", id));
    this.id = id;
    this.searchCriteria = null;
    this.message = null;

  }

  /**
   * Constructs a new {@code NotFoundException} with a message indicating no customers were found for the given search criteria.
   *
   * @param searchCriteria The search criteria that did not yield any results.
   */
  public NotFoundException(final Map<String, List<String>> searchCriteria) {
    super(String.format("No customers found with these search criteria: %s", searchCriteria));
    this.id = null;
    this.searchCriteria = searchCriteria;
    this.message = null;
  }

  public NotFoundException(final String message) {
    super("No customers found. with username: " + message);
    this.id = null;
    this.searchCriteria = null;
    this.message = message;
  }

  /**
   * Constructs a new {@code NotFoundException} with a default message indicating no customers were found.
   */
  public NotFoundException() {
    super("No customers found.");
    this.id = null;
    this.searchCriteria = null;
    this.message = null;
  }
}

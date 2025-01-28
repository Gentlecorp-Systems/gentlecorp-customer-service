/**
 * Contains custom exception classes for handling errors in the Gentle Bank customer domain.
 * <p>
 * This package defines exceptions that are specific to the Gentle Bank application,
 * providing meaningful error messages and status codes to handle various error scenarios
 * encountered during the processing of customer-related operations. These exceptions
 * help in managing errors gracefully and providing a better user experience by
 * returning appropriate error responses.
 * </p>
 *
 * <p>Exceptions in this package include:</p>
 * <ul>
 *   <li>{@link com.gentle.bank.customer.exception.AccessForbiddenException} - Thrown when access is forbidden due to insufficient roles.</li>
 *   <li>{@link com.gentle.bank.customer.exception.ConstraintViolationsException} - Thrown when validation constraints on a {@link com.gentle.bank.customer.dto.CustomerDTO} are violated.</li>
 *   <li>{@link com.gentle.bank.customer.exception.EmailExistsException} - Thrown when an email address already exists in the system.</li>
 *   <li>{@link com.gentle.bank.customer.exception.IllegalArgumentException} - Thrown when an invalid argument or key is encountered.</li>
 *   <li>{@link com.gentle.bank.customer.exception.NotFoundException} - Thrown when a customer or resource is not found based on the given criteria.</li>
 *   <li>{@link com.gentle.bank.customer.exception.SignUpException} - Thrown for errors occurring during the sign-up process.</li>
 *   <li>{@link com.gentle.bank.customer.exception.UnauthorizedException} - Thrown when a request is unauthorized.</li>
 *   <li>{@link com.gentle.bank.customer.exception.UsernameExistsException} - Thrown when a username already exists in the system.</li>
 *   <li>{@link com.gentle.bank.customer.exception.VersionInvalidException} - Thrown when the version provided is invalid.</li>
 *   <li>{@link com.gentle.bank.customer.exception.VersionOutdatedException} - Thrown when the version number is outdated.</li>
 * </ul>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
//TODO CommonExceptionHandler fehlt noch
package com.gentlecorp.customer.exception;

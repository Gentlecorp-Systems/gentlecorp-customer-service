package com.gentlecorp.customer.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static com.gentlecorp.customer.model.enums.ProblemType.PRECONDITION;
import static com.gentlecorp.customer.util.Constants.PROBLEM_PATH;

/**
 * Ausnahme, die ausgelöst wird, wenn eine ungültige Version angegeben wird.
 * <p>
 * Diese Ausnahme stellt sicher, dass fehlerhafte Versionsangaben als HTTP-Fehler behandelt werden.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public class VersionInvalidException extends ErrorResponseException {

  /**
   * Erstellt eine neue `VersionInvalidException` mit einer bestimmten HTTP-Statusmeldung.
   *
   * @param status  Der HTTP-Statuscode.
   * @param message Die Fehlermeldung.
   * @param uri     Die URI der Fehlerquelle.
   */
  public VersionInvalidException(final HttpStatusCode status, final String message, final URI uri) {
    this(status, message, uri, null);
  }

  /**
   * Erstellt eine neue `VersionInvalidException` mit zusätzlichen Fehlerdetails.
   *
   * @param status  Der HTTP-Statuscode.
   * @param message Die Fehlermeldung.
   * @param uri     Die URI der Fehlerquelle.
   * @param cause   Die ursprüngliche Ausnahme.
   */
  public VersionInvalidException(
      final HttpStatusCode status,
      final String message,
      final URI uri,
      final Throwable cause
  ) {
    super(status, asProblemDetail(status, message, uri), cause);
  }

  private static ProblemDetail asProblemDetail(final HttpStatusCode status, final String detail, final URI uri) {
    final var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setType(URI.create(PROBLEM_PATH + PRECONDITION.getValue()));
    problemDetail.setInstance(uri);
    return problemDetail;
  }
}

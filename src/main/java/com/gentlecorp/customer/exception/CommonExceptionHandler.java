package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.model.enums.ProblemType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;

import static com.gentlecorp.customer.util.Constants.PROBLEM_PATH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Diese Klasse behandelt häufige Ausnahmen in der Anwendung.
 * <p>
 * Sie konvertiert bekannte Fehler in standardisierte ProblemDetail-Objekte.
 * </p>
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {
  /**
   * Behandelt `NotFoundException` und gibt ein ProblemDetail-Objekt zurück.
   *
   * @param ex      Die `NotFoundException`.
   * @param request Die aktuelle HTTP-Anfrage.
   * @return Das ProblemDetail-Objekt.
   */
  @ExceptionHandler
  @ResponseStatus(NOT_FOUND)
  ProblemDetail onNotFoundException(
    final NotFoundException ex,
    final HttpServletRequest request
  ) {
    log.error("onNotFound: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.NOT_FOUND.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Behandelt `AccessForbiddenException` und gibt ein ProblemDetail-Objekt zurück.
   *
   * @param ex      Die `AccessForbiddenException`.
   * @param request Die aktuelle HTTP-Anfrage.
   * @return Das ProblemDetail-Objekt.
   */
  @ExceptionHandler
  @ResponseStatus(FORBIDDEN)
  ProblemDetail onAccessForbiddenException(final AccessForbiddenException ex, final HttpServletRequest request) {
    log.error("onAccessForbiddenException: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.FORBIDDEN.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler
  @ResponseStatus(BAD_REQUEST)
  ProblemDetail onIllegalArgumentException(final IllegalArgumentException ex, final HttpServletRequest request) {
    log.error("onIllegalArgumentException: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.BAD_REQUEST.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler
  @ResponseStatus(NOT_FOUND)
  ProblemDetail onNoResourceFoundException(final NoResourceFoundException ex, final HttpServletRequest request) {
    log.error("onNoResourceFoundException: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.NOT_FOUND.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }
}

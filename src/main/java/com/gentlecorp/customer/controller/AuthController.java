package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.UnauthorizedException;
import com.gentlecorp.customer.security.dto.TokenDTO;
import com.gentlecorp.customer.security.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Der `AuthController` verwaltet die Authentifizierungsprozesse für Benutzer.
 * Er nutzt Keycloak zur Verwaltung von Tokens und bietet Methoden zur Authentifizierung.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final CompromisedPasswordChecker passwordChecker;
  private final KeycloakService keycloakService;

  /**
   * Authentifiziert einen Benutzer mit Benutzername und Passwort und gibt ein Token zurück.
   *
   * @param username Der Benutzername des Nutzers.
   * @param password Das Passwort des Nutzers.
   * @return Ein `TokenDTO`, das ein JWT-Token enthält.
   * @throws UnauthorizedException Falls die Anmeldeinformationen ungültig sind.
   */
  @MutationMapping("authenticate")
  public TokenDTO login(@Argument("username") String username, @Argument("password") String password) {
//    String sanitizedLogin = login.toString().replace("\n", "").replace("\r", "");
//    log.debug("login: {}", sanitizedLogin);
//    final var username = login.username();
//    final var password = login.password();
    final TokenDTO result = keycloakService.login(username, password);

    if (result == null) {
      throw new UnauthorizedException("Benutzername oder Passwort sind falsch.");
    }
    return result;
  }

  /**
   * Ruft die Informationen des aktuellen Benutzers ab.
   *
   * @param jwt Das JWT-Token des authentifizierten Benutzers.
   * @return Eine Map mit Benutzerinformationen.
   */
  @MutationMapping("me")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
  public Map<String, Object> me(@AuthenticationPrincipal final Jwt jwt) {
    log.info("me: isCompromised() bei Passwort 'pass1234': {}", passwordChecker.check("pass1234").isCompromised());

    return Map.of(
      "subject", jwt.getSubject(),
      "claims", jwt.getClaims()
    );
  }


  /**
   * Behandelt eine `UnauthorizedException`, wenn falsche Anmeldedaten verwendet wurden.
   *
   * @param ex Die ausgelöste Ausnahme.
   * @return Eine `ResponseEntity` mit dem Fehlerstatus `UNAUTHORIZED` und einer Fehlermeldung.
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException ex) {
    return ResponseEntity.status(UNAUTHORIZED).body(ex.getMessage());
  }

//  @ExceptionHandler
//  @ResponseStatus(UNAUTHORIZED)
//  void onUnauthorized(@SuppressWarnings("unused") final HttpClientErrorException.Unauthorized ex) {
//  }
}

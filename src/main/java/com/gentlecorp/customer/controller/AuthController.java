package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.UnauthorizedException;
import com.gentlecorp.customer.model.dto.LoginDTO;
import com.gentlecorp.customer.model.dto.TokenDTO;
import com.gentlecorp.customer.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static com.gentlecorp.customer.util.Constants.AUTH_PATH;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final CompromisedPasswordChecker passwordChecker;
  private final KeycloakService keycloakService;

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

  @MutationMapping("me")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
  public Map<String, Object> me(@AuthenticationPrincipal final Jwt jwt) {
    log.info("me: isCompromised() bei Passwort 'pass1234': {}", passwordChecker.check("pass1234").isCompromised());

    return Map.of(
      "subject", jwt.getSubject(),
      "claims", jwt.getClaims()
    );
  }



  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException ex) {
    return ResponseEntity.status(UNAUTHORIZED).body(ex.getMessage());
  }

//  @ExceptionHandler
//  @ResponseStatus(UNAUTHORIZED)
//  void onUnauthorized(@SuppressWarnings("unused") final HttpClientErrorException.Unauthorized ex) {
//  }
}

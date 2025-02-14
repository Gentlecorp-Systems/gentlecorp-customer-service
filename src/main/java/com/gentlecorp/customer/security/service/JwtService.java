package com.gentlecorp.customer.security.service;


import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.UnauthorizedException;
import com.gentlecorp.customer.security.enums.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.security.enums.RoleType.ADMIN;
import static com.gentlecorp.customer.security.enums.RoleType.ELITE;
import static com.gentlecorp.customer.security.enums.RoleType.SUPREME;
import static com.gentlecorp.customer.security.enums.RoleType.USER;

/**
 * Service zur Verarbeitung von JWT-Token.
 * <p>
 * Dieser Service extrahiert Benutzerinformationen wie Benutzername, Benutzer-ID und Rollen aus einem JWT-Token.
 * </p>
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Service
@Slf4j
@SuppressWarnings("java:S5852")
public class JwtService {
  /**
   * Extrahiert den Benutzernamen aus dem JWT.
   *
   * @param jwt Das JWT-Token.
   * @return Der Benutzername.
   * @throws UnauthorizedException Falls das Token fehlt.
   */
  public String getUsername(final Jwt jwt) {
    if (jwt == null) {
      throw new UnauthorizedException("Missing Token");
    }
    final var username = (String) jwt.getClaims().get("preferred_username");
    log.debug("JwtService: username={}", username);
    return username;
  }

  /**
   * Extrahiert die Benutzer-ID aus dem JWT.
   *
   * @param jwt Das JWT-Token.
   * @return Die Benutzer-ID.
   * @throws NotFoundException Falls die Benutzer-ID nicht gefunden wird.
   */
  public String getUserID(final Jwt jwt) {
    log.debug("getUserID");
    if (jwt == null) {
      throw new NotFoundException();
    }
    final var id = (String) jwt.getClaims().get("sub");
    log.debug("getUserID: id={}", id);
    return id;
  }

  /**
   * Bestimmt die höchste Benutzerrolle aus dem JWT.
   *
   * @param jwt Das JWT-Token.
   * @return Die höchste Rolle als String.
   */
  public String getRole(final Jwt jwt) {
    final var realmRoles = getRealmRole(jwt);
    log.debug("JwtService: realmRoles={}", realmRoles);

    if (realmRoles.contains(ADMIN)) {
        return "ADMIN";
    }

    if (realmRoles.contains(USER)) {
      return "USER";
    }

    if (realmRoles.contains(SUPREME)) {
      return "SUPREME";
    }

    if (realmRoles.contains(ELITE)) {
      return "ELITE";
    }

      return "BASIC";
  }

  /**
   * Extrahiert die Rollen aus dem `realm_access`-Claim im JWT.
   *
   * @param jwt Das JWT-Token.
   * @return Eine Liste der extrahierten Rollen als `RoleType`.
   */
  public List<RoleType> getRealmRole(final Jwt jwt) {
    @SuppressWarnings("unchecked")
    final var realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
    final var rolesStr = realmAccess.get("roles");
    log.trace("JwtService:: rolesStr={}", rolesStr);

    return rolesStr
        .stream()
        .map(role -> {
          try {
            return RoleType.fromValue(role); // Verwende die fromValue-Methode
          } catch (IllegalArgumentException e) {
            log.warn("Unbekannte Rolle '{}' im JWT", role);
            return null; // Optionale Behandlung für ungültige Rollen
          }
        })
        .filter(Objects::nonNull) // Entferne ungültige Rollen
        .toList();
  }


  }

//  public List<RoleType> getClientRole(final Jwt jwt) {
//    @SuppressWarnings("unchecked")
//    final var resourceAccess = (Map<String, Map<String, List<String>>>) jwt.getClaims().get("resource_access");
//    final var client = resourceAccess.get("GentleBank");
//    final var rolesStr = client.get("roles");
//    log.trace("getClientRole: rolesStr={}", rolesStr);
//    return rolesStr
//      .stream()
//      .map(RoleType::of)
//      .filter(Objects::nonNull)
//      .toList();
//  }
//}

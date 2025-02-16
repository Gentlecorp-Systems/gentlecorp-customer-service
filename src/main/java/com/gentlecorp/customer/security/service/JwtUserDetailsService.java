package com.gentlecorp.customer.security.service;

import com.gentlecorp.customer.security.CustomUserDetails;
import com.gentlecorp.customer.security.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service zur Verarbeitung von JWTs und Erstellung von `UserDetails`.
 * <p>
 * Dieser Service analysiert ein JWT, extrahiert Benutzerinformationen und erstellt
 * ein entsprechendes `UserDetails`-Objekt für die Authentifizierung.
 * </p>
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailsService {

    /**
     * Erstellt ein `UserDetails`-Objekt aus einem JWT.
     *
     * @param jwt Das JWT, das analysiert wird.
     * @return Ein `UserDetails`-Objekt mit Benutzerinformationen.
     */
    public UserDetails loadUserDetailsFromJwt(Jwt jwt) {
        log.debug("Extrahiere UserDetails aus JWT: {}", jwt.getTokenValue());

        // ✅ Extrahiere Benutzername aus JWT (z. B. `preferred_username`)
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) {
            log.warn("JWT enthält keinen 'preferred_username'-Claim.");
            throw new IllegalArgumentException("JWT enthält keinen 'preferred_username'-Claim.");
        }

        // ✅ Extrahiere Realm-Rollen aus `realm_access.roles`
        List<String> roles = extractRoles(jwt);

        Collection<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toList());

        log.debug("Benutzer '{}' hat Rollen: {}", username, authorities);

        return new CustomUserDetails(username, authorities,jwt);
    }

    /**
     * Extrahiert die Rollen aus `realm_access.roles`, auch wenn sie verschachtelt sind.
     *
     * @param jwt Das JWT-Token
     * @return Eine Liste der extrahierten Rollen
     */
    private List<String> extractRoles(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

        if (roles == null) {
            // Falls `roles` null ist, versuche, sie aus einer verschachtelten Struktur zu extrahieren
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> extractedRoles = (List<String>) realmAccess.get("roles");
                // log.debug("Extracted roles: {}", extractedRoles);
                return extractedRoles != null
                    ? extractedRoles.stream()
                    .map(role -> role.replace(" ", "_").toUpperCase()) // Ersetzt Leerzeichen mit Unterstrichen
                    .filter(this::isValidRole) // Überprüft, ob die Rolle existiert
                    .toList()

                    : new ArrayList<>();
            }
        }
        return roles != null
            // ✅ Entferne Leerzeichen und konvertiere in Uppercase
            ? roles.stream()
                .map(role -> role.replace(" ", "_").toUpperCase()) // Ersetzt Leerzeichen mit Unterstrichen
                .filter(role -> isValidRole(role)) // Überprüft, ob die Rolle existiert
                .toList()
            : new ArrayList<>();
    }

    /**
     * Überprüft, ob eine Rolle in der `RoleType`-Enum existiert.
     *
     * @param role Der Rollenname
     * @return `true`, wenn die Rolle existiert, sonst `false`
     */
    private boolean isValidRole(String role) {
        try {
            RoleType.valueOf(role);
            return true;
        } catch (IllegalArgumentException e) {
            // log.warn("Unbekannte Rolle: {}", role);
            return false;
        }
    }
}

package com.gentlecorp.customer.config;

import com.gentlecorp.customer.security.JwtToUserDetailsConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

/**
 * Diese Schnittstelle definiert Sicherheitskonfigurationen für die Anwendung.
 * Sie konfiguriert Authentifizierungs- und Autorisierungsregeln für HTTP-Anfragen.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
sealed interface SecurityConfig permits ApplicationConfig {
  /**
   * Konfiguriert die Sicherheitsfilterkette für die Anwendung.
   *
   * @param httpSecurity Das HttpSecurity-Objekt zur Konfiguration der Sicherheitsrichtlinien.
   * @return Die konfigurierte `SecurityFilterChain`-Instanz.
   * @throws Exception Falls eine Sicherheitskonfiguration fehlschlägt.
   */
  @Bean
  default SecurityFilterChain securityFilterChain(
      final HttpSecurity httpSecurity,
      JwtToUserDetailsConverter converter
  ) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(authorize -> {
          authorize
              // ✅ Erlaubt alle GraphQL-Anfragen (Schema-Ladung & Queries)
              .requestMatchers(POST, "/graphql").permitAll()

              .requestMatchers(
                  // Actuator: Health for liveness and readiness for Kubernetes
                  EndpointRequest.to(HealthEndpoint.class),
                  // Actuator: Prometheus for monitoring
                  EndpointRequest.to(PrometheusScrapeEndpoint.class)
              ).permitAll()
              // OpenAPI or Swagger UI and GraphiQL
              .requestMatchers(GET, "/v3/api-docs.yaml", "/v3/api-docs", "/graphiql").permitAll()
              .requestMatchers("/error", "/error/**").permitAll()

              .anyRequest().authenticated();
        })
        //.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
        // Spring Security does not create or use HttpSession for SecurityContext
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
        .build();
  }
  /**
   * Definiert die Passwortkodierung für die Anwendung.
   *
   * @return Ein PasswordEncoder zur sicheren Speicherung von Passwörtern.
   */
  @Bean
  default PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  @Bean
  default CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }

  /**
   * Konfiguriert die CORS-Regeln der Anwendung.
   *
   * @return Die CORS-Konfigurationsquelle.
   */
//  @Bean
//  default CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://mydomain.com", "http://localhost:3000"));
//    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//    configuration.setExposedHeaders(List.of("Authorization"));
//    configuration.setAllowCredentials(true);
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }

  /**
   * Definiert den JWT-Converter für die Sicherheitskonfiguration.
   * Hier wird sichergestellt, dass Rollen korrekt extrahiert und zugewiesen werden.
   *
   * @return Ein Converter, der JWT in AbstractAuthenticationToken umwandelt.
   */
//  default JwtAuthenticationConverter jwtAuthenticationConverter() {
//    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
//      Collection<GrantedAuthority> authorities = new ArrayList<>();
//
//      // Realm-Rollen hinzufügen
//      List<String> realmRoles = jwt.getClaimAsStringList("realm_access.roles");
//      if (realmRoles != null) {
//        realmRoles.stream()
//            .map(role -> new SimpleGrantedAuthority(String.format("ROLE_%s", role)))
//            .forEach(authorities::add);
//      }
//
//      // Client-spezifische Rollen (z.B. rolemapper)
//      Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
//      if (resourceAccess != null) {
//        @SuppressWarnings("unchecked")
//        Map<String, Object> roleMapper = (Map<String, Object>) resourceAccess.get("rolemapper");
//        if (roleMapper != null && roleMapper.containsKey("roles")) {
//          @SuppressWarnings("unchecked")
//          List<String> clientRoles = (List<String>) roleMapper.get("roles");
//          clientRoles.stream()
//              .map(role -> new SimpleGrantedAuthority(String.format("ROLE_%s", role)))
//              .forEach(authorities::add);
//        }
//      }
//      return authorities;
//    });
//
//    return jwtAuthenticationConverter;
//  }
}
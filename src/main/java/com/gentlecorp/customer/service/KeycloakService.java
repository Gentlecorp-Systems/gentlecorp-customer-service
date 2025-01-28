package com.gentlecorp.customer.service;

import com.gentlecorp.customer.KeycloakProps;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.SignUpException;
import com.gentlecorp.customer.model.dto.TokenDTO;
import com.gentlecorp.customer.model.dto.UserRepresentation;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.repository.KeycloakRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

  private final KeycloakRepository keycloakRepository;
  private final KeycloakProps keycloakProps;
  private String clientAndSecretEncoded;
  private final JwtService jwtService;

  @PostConstruct
  private void encodeClientAndSecret() {
    final var clientAndSecret = keycloakProps.clientId() + ':' + keycloakProps.clientSecret();
    clientAndSecretEncoded = Base64
      .getEncoder()
      .encodeToString(clientAndSecret.getBytes(Charset.defaultCharset()));
  }

  public TokenDTO login(final String username, final String password) {
    return keycloakRepository.login(
      "grant_type=password&username=" + username
        + "&password=" + password
        + "&client_id=" + keycloakProps.clientId()
        + "&client_secret=" + keycloakProps.clientSecret()
        + "&scope=openid",
      "Basic " + clientAndSecretEncoded,
      APPLICATION_FORM_URLENCODED_VALUE
    );
  }

  private String getAdminToken() {
    log.debug("getAdminToken");
    final var adminToken = login("admin", "p");
    return adminToken.accessToken();
  }

  private String getUserInfo(final String token) {
    log.debug("getUserInfo: token={}", token);

    final var info = keycloakRepository.userInfo("Bearer " + token, APPLICATION_FORM_URLENCODED_VALUE);
    return info.sub();
  }

  public void signIn(final Customer customer, final String password, final String role) {
    log.debug("signIn: customer={}", customer);

    // JSON data for registration
    final var customerData = """
            {
                "username": "%s",
                "enabled": true,
                "firstName": "%s",
                "lastName": "%s",
                "email": "%s",
                "credentials": [{
                    "type": "password",
                    "value": "%s",
                    "temporary": false
                }]
            }
            """.formatted(
      customer.getUsername(),
      customer.getFirstName(),
      customer.getLastName(),
      customer.getEmail(),
      password // Ensure password is present in Customer object
    );

    log.debug("signIn: customerData={}", customerData);

    try {
      // Register user in Keycloak and get user ID
      final var response = keycloakRepository.signIn(
        customerData,
        "Bearer " + getAdminToken(),
        APPLICATION_JSON_VALUE
      );
      log.info("signIn: Customer registered in Keycloak");

      final var accessToken = login(customer.getUsername(), password).accessToken();
      final var userId = getUserInfo(accessToken);
      log.debug("signIn: userId={}", userId);

      // Assign role to user
      assignRoleToUser(userId, role);

    } catch (Exception e) {
      log.error("Error during user registration: ", e);
      throw new SignUpException("User registration failed: " + e.getMessage());
    }
  }

  private void assignRoleToUser(String userId, String roleName) {
    log.debug("Assigning role {} to user {}", roleName, userId);

    final var token = getAdminToken();
    final var roleId = getRole(roleName, token);

    // JSON data for role assignment
    final var roleData = """
            [{
                "id": "%s",
                "name": "%s"
            }]
            """.formatted(roleId, roleName);

    log.debug("roleData={}", roleData);
    try {
      keycloakRepository.assignRoleToUser(
        roleData,
        "Bearer " + getAdminToken(),
        APPLICATION_JSON_VALUE,
        userId
      );

    } catch (Exception e) {
      log.error("Error assigning role to user: ", e);
      throw new RuntimeException("Failed to assign role to user: " + e.getMessage());
    }
  }

  private String getRole(final String roleName, final String token) {
    log.debug("getRole: roleName={}, token={}", roleName, token);

    final var roles = keycloakRepository.getRoles("Bearer " + token, APPLICATION_JSON_VALUE);
    log.debug("getRole: roles={}", roles);

    final var role = roles.stream()
      .filter(r -> r.name().equals(roleName)).findFirst().orElse(null);

    if (role == null) {
      throw new RuntimeException("RoleDTO not found: " + roleName);
    }
    log.debug("getRole: role={}", role);
    return role.id();
  }

  public void update(final Customer customer, final Jwt jwt, final boolean isAdmin, final String oldUsername) {
    log.debug("update: customer={} isAdmin={}", customer, isAdmin);

    // Retrieve user ID based on access token
    String userId;
    if (isAdmin)  {
      final var token = "Bearer " + jwt.getTokenValue();
      final var userList = keycloakRepository.getUserByUsername(token, oldUsername);
      userId = userList.stream()
        .map(UserRepresentation::id)
        .findFirst().orElseThrow(() -> new NotFoundException(customer.getUsername()));
    } else {
       userId = jwtService.getUserID(jwt);
    }
    log.debug("update: userId={}", userId);
    // JSON data for user update
    final var userData = """
          {
            "firstName": "%s",
            "lastName": "%s",
            "email": "%s",
            "username": "%s",
            "enabled": true
          }
          """.formatted(
      customer.getFirstName(),
      customer.getLastName(),
      customer.getEmail(),
      customer.getUsername()
    );
    log.debug("update: userData={}", userData);

    final var adminToken = isAdmin
      ? jwt.getTokenValue()
      : getAdminToken();
    try {
      // Call repository to update user in Keycloak
      keycloakRepository.updateUser(
        userData,
        "Bearer " + adminToken,
        APPLICATION_JSON_VALUE,
        userId
      );
    } catch (Exception e) {
      log.error("Error updating user: ", e);
      throw new RuntimeException("Failed to update user: " + e.getMessage());
    }
  }

  public void updatePassword(String newPassword, final Jwt jwt) {

    final var userId = jwtService.getUserID(jwt);

    final var passwordData = """
          {
            "type": "password",
            "value": "%s",
            "temporary": false
          }
          """.formatted(newPassword);

    log.debug("updatePassword: passwordData={}", passwordData);

    try {
      keycloakRepository.updateUserPassword(
        passwordData,
        "Bearer " + getAdminToken(),
        APPLICATION_JSON_VALUE,
        userId
      );
    } catch (Exception e) {
      log.error("Error updating password for user {}: ", userId, e);
      throw new RuntimeException("Failed to update password for user: " + e.getMessage());
    }
  }

  public void delete(final String token, final String username) {
    log.debug("delete: username={}", username);
    final var userList = keycloakRepository.getUserByUsername(token, username);
    final var userId = userList.stream()
      .map(UserRepresentation::id)
      .findFirst().orElseThrow(() -> new NotFoundException(username));

    keycloakRepository.deleteUser(token, userId);
  }
}

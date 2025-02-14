package com.gentlecorp.customer.security;

import com.gentlecorp.customer.security.dto.RoleDTO;
import com.gentlecorp.customer.security.dto.TokenDTO;
import com.gentlecorp.customer.security.dto.UserInfoDTO;
import com.gentlecorp.customer.security.dto.UserRepresentation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Repository für die Kommunikation mit dem Keycloak-Server.
 * <p>
 * Stellt Endpunkte für Benutzerverwaltung, Authentifizierung und Rollenmanagement bereit.
 * </p>
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@HttpExchange
public interface KeycloakRepository {
  @GetExchange("http://localhost:18080/auth/realms/camunda-platform/.well-known/openid-configuration")
  Map<String, Object> openidConfiguration();

  @PostExchange("auth/realms/camunda-platform/protocol/openid-connect/token")
  TokenDTO login(
    @RequestBody String loginData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PostExchange("auth/admin/realms/camunda-platform/users")
  HttpResponse<Void> signIn(
    @RequestBody String customer,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PostExchange("auth/admin/realms/camunda-platform/users/{userId}/role-mappings/realm")
  void assignRoleToUser(
    @RequestBody String roleData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @PostExchange("auth/realms/camunda-platform/protocol/openid-connect/userinfo")
  UserInfoDTO userInfo(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @GetExchange("auth/admin/realms/camunda-platform/roles")
  Collection<RoleDTO> getRoles(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PutExchange("auth/admin/realms/camunda-platform/users/{userId}")
  void updateUser(
    @RequestBody String userData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @PutExchange("auth/admin/realms/camunda-platform/users/{userId}/reset-password")
  void updateUserPassword(
    @RequestBody String passwordData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @DeleteExchange("auth/admin/realms/camunda-platform/users/{userId}")
  void deleteUser(
    @RequestHeader(AUTHORIZATION) String authorization,
    @PathVariable("userId") String userId
  );

  @GetExchange("auth/admin/realms/camunda-platform/users?username={username}")
  List<UserRepresentation> getUserByUsername(
    @RequestHeader(AUTHORIZATION) String authorization,
    @PathVariable("username") String username
  );

}

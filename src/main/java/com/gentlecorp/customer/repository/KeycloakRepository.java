package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.dto.RoleDTO;
import com.gentlecorp.customer.model.dto.TokenDTO;
import com.gentlecorp.customer.model.dto.UserInfoDTO;
import com.gentlecorp.customer.model.dto.UserRepresentation;
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

@HttpExchange
public interface KeycloakRepository {
  @GetExchange("http://localhost:8880/realms/GentleCorp-Ecosystem/.well-known/openid-configuration")
  Map<String, Object> openidConfiguration();

  @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/token")
  TokenDTO login(
    @RequestBody String loginData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PostExchange("/admin/realms/GentleCorp-Ecosystem/users")
  HttpResponse<Void> signIn(
    @RequestBody String customer,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PostExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}/role-mappings/realm")
  void assignRoleToUser(
    @RequestBody String roleData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/userinfo")
  UserInfoDTO userInfo(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @GetExchange("/admin/realms/GentleCorp-Ecosystem/roles")
  Collection<RoleDTO> getRoles(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PutExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}")
  void updateUser(
    @RequestBody String userData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @PutExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}/reset-password")
  void updateUserPassword(
    @RequestBody String passwordData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @DeleteExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}")
  void deleteUser(
    @RequestHeader(AUTHORIZATION) String authorization,
    @PathVariable("userId") String userId
  );

  @GetExchange("/admin/realms/GentleCorp-Ecosystem/users?username={username}")
  List<UserRepresentation> getUserByUsername(
    @RequestHeader(AUTHORIZATION) String authorization,
    @PathVariable("username") String username
  );

}

package com.gentlecorp.customer.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;

@Component
@Slf4j
public class UriHelper {
  private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
  private static final String X_FORWARDED_HOST = "x-forwarded-host";
  private static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";
  private static final String CUSTOMER_PREFIX = "/customer";

  public URI getBaseUri(final HttpServletRequest request) {
    final var forwardedHost = request.getHeader(X_FORWARDED_HOST);
    if (forwardedHost != null) {
      // Forwarding through Kubernetes Ingress Controller or Spring Cloud Gateway
      return getBaseUriForwarded(request, forwardedHost);
    }

    // No forwarding from an API Gateway
    // URI from scheme, host, port, and path
    final var uriComponents = ServletUriComponentsBuilder.fromRequestUri(request).build();
    final var baseUri = String.format("%s://%s:%s%s",
      uriComponents.getScheme(),
      uriComponents.getHost(),
      uriComponents.getPort(),
      CUSTOMER_PATH);
    log.debug("getBaseUri (without forwarding): baseUri={}", baseUri);
    return URI.create(baseUri);
  }

  private URI getBaseUriForwarded(final HttpServletRequest request, final String forwardedHost) {
    // x-forwarded-host = Hostname of the API Gateway

    // "https" or "http"
    final var forwardedProto = request.getHeader(X_FORWARDED_PROTO);
    if (forwardedProto == null) {
      throw new IllegalStateException(String.format("No \"%s\" header present", X_FORWARDED_PROTO));
    }

    var forwardedPrefix = request.getHeader(X_FORWARDED_PREFIX);
    // x-forwarded-prefix: null for Kubernetes Ingress Controller or "/customer" for Spring Cloud Gateway
    if (forwardedPrefix == null) {
      log.trace("getBaseUriForwarded: No \"{}\" header present", X_FORWARDED_PREFIX);
      forwardedPrefix = CUSTOMER_PREFIX;
    }

    final var baseUri = String.format("%s://%s%s%s", forwardedProto, forwardedHost, forwardedPrefix, CUSTOMER_PATH);
    log.debug("getBaseUriForwarded: baseUri={}", baseUri);
    return URI.create(baseUri);
  }

  public URI createUri(HttpServletRequest request, UUID id) throws URISyntaxException {
    final var baseUri = getBaseUri(request);
    return new URI(String.format("%s/%s", baseUri.toString(), id));
  }
}

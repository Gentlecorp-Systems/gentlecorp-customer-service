package com.gentlecorp.customer.util;

import com.gentlecorp.customer.exception.VersionAheadException;
import com.gentlecorp.customer.exception.VersionInvalidException;
import com.gentlecorp.customer.exception.VersionOutdatedException;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Optional;

import static com.gentlecorp.customer.util.Constants.VERSION_NUMBER_MISSING;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;

@Slf4j
public class VersionUtils {

  public static int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
    log.trace("getVersion: {}", versionOpt);
    return versionOpt.map(versionStr -> {
      if (isValidVersion(versionStr)) {
        return Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
      } else {
        throw new VersionInvalidException(
          PRECONDITION_FAILED,
          String.format("Invalid ETag %s", versionStr), // Korrektur der String-Interpolation
          URI.create(request.getRequestURL().toString())
        );
      }
    }).orElseThrow(() -> new VersionInvalidException(
      PRECONDITION_REQUIRED,
      VERSION_NUMBER_MISSING,
      URI.create(request.getRequestURL().toString())
    ));
  }

  private static boolean isValidVersion(String versionStr) {
    log.debug("length of versionString={} versionString={}", versionStr.length(), versionStr);
    return versionStr.length() >= 3 &&
      versionStr.charAt(0) == '"' &&
      versionStr.charAt(versionStr.length() - 1) == '"';
  }

  public static void validateVersion(int version, Customer entity) {
    if (version < entity.getVersion()) {
      log.error("Version is outdated");
      throw new VersionOutdatedException(version);
    }
    if (version > entity.getVersion()) {
      log.error("Version is ahead of the current version");
      throw new VersionAheadException(version);
    }
  }

  public static void validateVersion(int version, Contact entity) {
    if (version < entity.getVersion()) {
      log.error("Version is outdated");
      throw new VersionOutdatedException(version);
    }
    if (version > entity.getVersion()) {
      log.error("Version is ahead of the current version");
      throw new VersionAheadException(version);
    }
  }
}

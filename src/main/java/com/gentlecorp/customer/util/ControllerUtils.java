package com.gentlecorp.customer.util;

import java.util.Objects;
import java.util.Optional;

public class ControllerUtils {
  public static String createETag(int version) {
    return String.format("\"%s\"", version);
  }

  public static boolean isETagMatching(Optional<String> requestVersion, String currentVersion) {
    return Objects.equals(requestVersion.orElse(null), currentVersion);
  }
}

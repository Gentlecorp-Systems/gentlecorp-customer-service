package com.gentlecorp.customer.util;

import java.util.regex.Pattern;

public class Constants {
  public static final String PROBLEM_PATH = "/problem";
  public static final String CUSTOMER_PATH = "/customer";
  public static final String AUTH_PATH = "/auth";
  public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";
  public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";

  public static final int MIN_LENGTH = 8;
  public static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
  public static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
  public static final Pattern NUMBERS = Pattern.compile(".*\\d.*");
  @SuppressWarnings("RegExpRedundantEscape")
  public static final Pattern SYMBOLS = Pattern.compile(".*[!-/:-@\\[-`{-\\~].*");
}

package com.gentlecorp.customer.util;

import java.util.regex.Pattern;

/**
 * Definiert allgemeine Konstanten für die Anwendung.
 * <p>
 * Enthält reguläre Ausdrücke, Validierungsregeln und andere wiederverwendbare Werte.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public class Constants {
  /** Basis-URL für Problem-Details */
  public static final String PROBLEM_PATH = "/problem";
  /** Regulärer Ausdruck zur Überprüfung von UUIDs */
  public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";
  /** Fehlermeldung für fehlende Versionsnummern */
  public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";

  /** Minimale Länge für Passwörter */
  public static final int MIN_LENGTH = 8;

  /** Reguläre Ausdrücke zur Passwortprüfung */
  public static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
  public static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
  public static final Pattern NUMBERS = Pattern.compile(".*\\d.*");
  @SuppressWarnings("RegExpRedundantEscape")
  public static final Pattern SYMBOLS = Pattern.compile(".*[!-/:-@\\[-`{-\\~].*");
}

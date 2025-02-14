package com.gentlecorp.customer.util;

import com.gentlecorp.customer.config.EnvConfig;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Erstellt ein ASCII-Banner mit Systeminformationen für die Anwendung.
 * <p>
 * Zeigt unter anderem Java-Version, Betriebssystem, Speicherverbrauch, Kubernetes- und Keycloak-Konfiguration an.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public final class Banner {

  private static final Figlets figlets = new Figlets();
  private static final String FIGLET = figlets.randomFigletGenerator();

  // Kubernetes
  private static final String SERVICE_HOST = System.getenv("CUSTOMER_SERVICE_HOST");
  private static final String SERVICE_PORT = System.getenv("CUSTOMER_SERVICE_PORT");
  private static final String KUBERNETES = (SERVICE_HOST == null || SERVICE_PORT == null)
      ? "N/A"
      : String.format("CUSTOMER_SERVICE_HOST=%s, CUSTOMER_SERVICE_PORT=%s", SERVICE_HOST, SERVICE_PORT);

  // Keycloak
  private static final String KEYCLOAK_HOST = EnvConfig.get("KEYCLOAK_HOST");
  private static final String KEYCLOAK_PORT = EnvConfig.get("KEYCLOAK_PORT");
  private static final String KEYCLOAK = (KEYCLOAK_HOST == null || KEYCLOAK_PORT == null)
      ? "N/A"
      : String.format("KEYCLOAK_HOST=%s, KEYCLOAK_PORT=%s", KEYCLOAK_HOST, KEYCLOAK_PORT);

  // MongoDB
  private static final String MONGODB_USER = EnvConfig.get("MONGODB_USER_NAME");
  private static final String MONGODB_PASS = EnvConfig.get("MONGODB_USER_PASSWORT");
  private static final String MONGODB = (MONGODB_USER == null || MONGODB_PASS == null)
      ? "N/A"
      : String.format("MONGODB_USER_NAME=%s", MONGODB_USER);

  private static final InetAddress LOCALHOST = resolveLocalhost();

  public static final String TEXT = buildBannerText();

  private Banner() {
    // Private constructor to prevent instantiation
  }

  private static InetAddress resolveLocalhost() {
    try {
      return InetAddress.getLocalHost();
    } catch (final UnknownHostException ex) {
      throw new IllegalStateException("Unable to resolve localhost", ex);
    }
  }

  private static String buildBannerText() {
    return String.format("""
            %s
            (C) Caleb Gyamfi, GentleCorp-System
            Version             2024.08.24
            Spring Boot         %s
            Spring Security     %s
            Spring Framework    %s
            Java                %s - %s
            Betriebssystem      %s
            Rechnername         %s
            IP-Adresse          %s
            Heap: Size          %d MiB
            Heap: Free          %d MiB
            Kubernetes          %s
            Keycloak            %s
            MongoDB             %s
            Username            %s
            JVM Locale          %s
            """,
        FIGLET,
        SpringBootVersion.getVersion(),
        SpringSecurityCoreVersion.getVersion(),
        SpringVersion.getVersion(),
        Runtime.version(),
        System.getProperty("java.vendor"),
        System.getProperty("os.name"),
        LOCALHOST.getHostName(),
        LOCALHOST.getHostAddress(),
        Runtime.getRuntime().totalMemory() / (1024L * 1024L),
        Runtime.getRuntime().freeMemory() / (1024L * 1024L),
        KUBERNETES,
        KEYCLOAK,
        MONGODB,
        System.getProperty("user.name"),
        Locale.getDefault().toString()
    );
  }
}

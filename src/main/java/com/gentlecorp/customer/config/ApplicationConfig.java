package com.gentlecorp.customer.config;

/**
 * Diese Klasse stellt die zentrale Konfigurationsklasse der Anwendung dar.
 * Sie implementiert verschiedene Konfigurationsschnittstellen, um eine
 * modulare und erweiterbare Architektur zu gewährleisten.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public final class ApplicationConfig implements SecurityConfig, KeycloakClientConfig, EnvConfig {

  /**
   * Privater Konstruktor, um Instanzen dieser Klasse zu verhindern.
   */
  ApplicationConfig() {
  }
}

package com.gentlecorp.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Konfigurationseigenschaften für den E-Mail-Versand.
 * <p>
 * Diese Klasse lädt E-Mail-spezifische Einstellungen aus `application.yml`.
 * </p>
 *
 * @since 14.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@ConfigurationProperties(prefix = "app.mail")
@Setter
@Getter
@AllArgsConstructor
public class MailProps {
  private String from;
  private String to;
}

package com.gentlecorp.customer.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Diese Schnittstelle ermöglicht den Zugriff auf Umgebungsvariablen aus einer
 * `.env`-Datei. Sie nutzt die Bibliothek `dotenv`, um Konfigurationswerte sicher zu laden.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
public interface EnvConfig {

    /**
     * Instanz des Dotenv-Konfigurationsladers zur Verarbeitung der `.env`-Datei.
     */
    Dotenv DOTENV = Dotenv.configure()
        .filename(".env")
        .load();

    /**
     * Ruft den Wert einer Umgebungsvariablen anhand ihres Schlüssels ab.
     *
     * @param key Der Schlüssel der Umgebungsvariable.
     * @return Der entsprechende Wert oder {@code null}, falls der Schlüssel nicht existiert.
     */
    static String get(String key) {
        return DOTENV.get(key);
    }
}

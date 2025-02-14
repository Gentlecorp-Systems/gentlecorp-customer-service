package com.gentlecorp.customer.exception;

import lombok.Getter;

/**
 * Diese Ausnahme wird ausgelöst, wenn ein ungültiges Argument an eine Methode übergeben wird.
 * <p>
 * Die Exception gibt entweder einen ungültigen Schlüssel oder eine ungültige Stufe (`tier`) an.
 * </p>
 *
 * @since 13.02.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public final class IllegalArgumentException extends RuntimeException {
    /** Der ungültige Schlüssel, falls vorhanden. */
    private final String key;
    /** Die ungültige Tier-Stufe, falls vorhanden. */
    private final int tier;

    /**
     * Erstellt eine neue `IllegalArgumentException` für einen ungültigen Schlüssel.
     *
     * @param key Der ungültige Schlüssel.
     */
    public IllegalArgumentException(final String key) {
      super(String.format("Invalid key: %s", key));
        this.key = key;
        this.tier = 0;
    }

    /**
     * Erstellt eine neue `IllegalArgumentException` für eine ungültige Stufenangabe.
     *
     * @param tier Die ungültige Tier-Stufe.
     */
  public IllegalArgumentException(final int tier) {
    super(String.format("Invalid tier level: %s", tier));
    this.key = null;
    this.tier = 0;
  }
}

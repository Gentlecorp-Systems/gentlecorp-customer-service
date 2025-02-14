package com.gentlecorp.customer.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * Diese Klasse enthält die Konfigurationseinstellungen für die MongoDB-Datenbankverbindung.
 * Sie stellt eine Methode bereit, um einen konfigurierten MongoDB-Client zu initialisieren.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Configuration
public class MongoConfig {

    /**
     * Die Verbindungs-URI für die MongoDB-Datenbank.
     */
    @Value("${app.mongo.uri}")
    private String mongoUri;

    /**
     * Erstellt und konfiguriert eine MongoDB-Client-Instanz.
     *
     * @return Ein konfigurierter MongoDB-Client.
     */
    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(new com.mongodb.ConnectionString(mongoUri))
            .build();
        return MongoClients.create(settings);
    }
}

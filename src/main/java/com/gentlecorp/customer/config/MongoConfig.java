package com.gentlecorp.customer.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MongoConfig {

    @Value("${app.mongo.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD) // Standard UUID-Format festlegen
            .applyConnectionString(new com.mongodb.ConnectionString(mongoUri)) // Verbindung aus Umgebungsvariablen lesen
            .build();
        return MongoClients.create(settings);
    }
}

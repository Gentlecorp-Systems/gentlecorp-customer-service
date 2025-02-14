package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

/**
 * Repository für die Verwaltung der `Customer`-Entität in der MongoDB-Datenbank.
 * <p>
 * Dieses Interface erweitert `MongoRepository`, um CRUD-Operationen sowie erweiterte Abfragen für Kunden bereitzustellen.
 * </p>
 *
 * @since 13.02.2025
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, UUID> {

    /**
     * Findet Kunden basierend auf dynamischen Filtern, Paginierung und Sortierung.
     * <p>
     * Die Filterparameter werden als JSON-ähnliche `String`-Darstellung erwartet und in eine dynamische MongoDB-Abfrage umgewandelt.
     * </p>
     *
     * @param filter   Dynamische Suchkriterien als JSON-ähnliche Zeichenkette.
     * @param pageable Paginierungsparameter (Seite, Größe, Sortierung).
     * @return Eine `Page<Customer>` mit den gefundenen Kunden.
     */
    @Query("{}") // Nutze eine native MongoDB Query für dynamische Filter
    Page<Customer> find(String filter, Pageable pageable);
}


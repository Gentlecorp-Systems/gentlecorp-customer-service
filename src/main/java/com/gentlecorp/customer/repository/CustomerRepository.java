package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, UUID> {

    /**
     * Findet Kunden basierend auf dynamischen Filtern, Paginierung und Sortierung.
     *
     * @param filter    Dynamische Suchkriterien als JSON-ähnliche Map.
     * @param pageable  Paginierungsparameter (Seite, Größe, Sortierung).
     * @return Eine `Page<Customer>` mit den gefundenen Kunden.
     */
    @Query("{}") // Nutze eine native MongoDB Query für dynamische Filter
    Page<Customer> find(String filter, Pageable pageable);
}


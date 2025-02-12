package com.gentlecorp.customer.service;

import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.security.RoleType;
import com.gentlecorp.customer.repository.CustomerRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.gentlecorp.customer.security.RoleType.ADMIN;
import static com.gentlecorp.customer.security.RoleType.USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerReadService {

    @Autowired
    private MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;


    /**
     * Findet einen Kunden anhand der ID.
     *
     * @param id   Die Kunden-ID
     * @param user Der eingeloggte Benutzer
     * @return Der gefundene Kunde
     * @throws NotFoundException Falls der Kunde nicht gefunden wird
     * @throws AccessForbiddenException Falls der Benutzer keinen Zugriff hat
     */
    @Observed(name = "find-by-id")
    public @NonNull Customer findById(final UUID id, final UserDetails user) {
        log.debug("findById: id={}", id);
        final var customer = customerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(id));

        if (customer.getUsername().equals(user.getUsername())) {
            return customer;
        }

        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .toList();

        if (!roles.contains(ADMIN) && !roles.contains(USER)) {
            throw new AccessForbiddenException(user.getUsername(),roles);
        }

        log.debug("findById: customer={}", customer);
        return customer;
    }

    /**
     * Führt eine dynamische Filter-, Paginierungs- und Sortierabfrage aus.
     *
     * @param filter Die Filterbedingungen als `Map<String, Object>`
     * @param page   Die gewünschte Seite
     * @param size   Die Anzahl der Einträge pro Seite
     * @param sort   Die Sortierkriterien als `Map<String, String>` (Feldname -> "ASC"/"DESC")
     * @return Eine Liste mit den gefundenen Kunden
     */
    public @NonNull Collection<Customer> find(
        Map<String, Object> filter, int page, int size, Map<String, String> sort
    ) {
        log.debug("find: filter={}, page={}, size={}, sort={}", filter, page, size, sort);

        // Sortierung korrekt umwandeln
        List<Sort.Order> orders = sort.entrySet()
            .stream()
            .map(entry -> new Sort.Order(
                "ASC".equalsIgnoreCase(entry.getValue()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                entry.getKey()
            ))
            .toList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        // MongoDB-Query ausführen
        Query query = new Query();
        if (!filter.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filter.entrySet().stream()
                .map(e -> Criteria.where(e.getKey()).is(e.getValue()))
                .toArray(Criteria[]::new)));
        }
        query.with(pageable);

        List<Customer> customers = mongoTemplate.find(query, Customer.class);
        log.debug("find: customers={}", customers);
        return customers;
    }



}

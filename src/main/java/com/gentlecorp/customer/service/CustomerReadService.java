package com.gentlecorp.customer.service;

import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.repository.CustomerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerReadService {
    private final CustomerRepository customerRepository;


    public @NonNull Customer findById(
        final UUID id,
        final Jwt jwt,
        final boolean fetchAll
    ) {
        log.debug("findById: id={}, fetchAll={}", id, fetchAll);
        final var customer = customerRepository.findById(id).orElseThrow();
        log.debug("findById: customer={}", customer);
        return customer;
    }

    public @NonNull Collection<Customer> find(@NonNull final Map<String, List<String>> searchCriteria) {
        log.debug("find: searchCriteria={}", searchCriteria);
            return customerRepository.findAll();
    }
}

package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.model.CustomerModel;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.service.CustomerReadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.customer.util.ControllerUtils.createETag;
import static com.gentlecorp.customer.util.ControllerUtils.isETagMatching;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final CustomerReadService customerReadService;

    @QueryMapping("customer")
    @PreAuthorize("hasAnyRole('Admin')")
    Customer getById(
        @Argument final UUID id,
        final Authentication authentication
    ) {
        final var user = (UserDetails) authentication.getPrincipal();
        log.debug("findById: id={}, user={}", id, user);
        final var customer = customerReadService.findById(id,user,true);
        log.debug("findById: customer={}", customer);
        return customer;
    }

    @QueryMapping("customer")
    @PreAuthorize("hasRole('Admin')")
    Collection<Customer> find(@Argument final Optional<SearchCriteria> input) {
        log.debug("find: input={}", input);
        final var suchkriterien = input.map(SearchCriteria::toMap).orElse(emptyMap());
        final var customers = customerReadService.find(suchkriterien);
        log.debug("find: customer={}", customers);
        return customers;
    }

    @QueryMapping("hallo")
    public String hello() {
        return "Hello, GraphQL!";
    }
}

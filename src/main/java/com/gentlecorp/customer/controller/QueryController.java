package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.UnauthorizedException;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.input.FilterInput;
import com.gentlecorp.customer.model.input.PaginationInput;
import com.gentlecorp.customer.model.input.SortInput;
import com.gentlecorp.customer.security.CustomUserDetails;
import com.gentlecorp.customer.service.CustomerReadService;
import com.gentlecorp.customer.security.service.JwtService;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;
import static org.springframework.graphql.execution.ErrorType.FORBIDDEN;
import static org.springframework.graphql.execution.ErrorType.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Der `QueryController` verarbeitet GraphQL-Anfragen für Kunden und Benutzer.
 * Er stellt Methoden für das Abrufen von Kunden und anderen Benutzerdaten bereit.
 *
 * @since 13.02.2024
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final CustomerReadService customerReadService;

    /**
     * Ruft einen Kunden anhand seiner ID ab.
     *
     * @param id Die UUID des Kunden.
     * @param authentication Die Authentifizierungsinformationen des Nutzers.
     * @return Das gefundene `Customer`-Objekt.
     */
    @QueryMapping("customer")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    Customer getById(
        @Argument final UUID id,
        final Authentication authentication
    ) {
        log.debug("deleteCustomer: id={}", authentication);
        log.debug("findById: id={}, user={}", id, authentication);

        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var customer = customerReadService.findById(id,user);
        log.debug("findById: customer={}", customer);
        return customer;
    }

    /**
     * GraphQL-Query für `customers`.
     *
     * @param filter     Die Filterbedingungen als `FilterInput`.
     * @param pagination Die Paginierungsparameter.
     * @param order      Die Sortierkriterien.
     * @return Eine Liste der gefundenen Kunden.
     */
    @QueryMapping("customers")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Collection<Customer> findCustomers(
        @Argument FilterInput filter,
        @Argument PaginationInput pagination,
        @Argument SortInput order
    ) {
        log.debug("findCustomers: filter={}, pagination={}, order={}", filter, pagination, order);

        // ✅ Konvertiere den Filter in eine Map für MongoDB
        final Map<String, Object> filterMap = filter != null ? filter.toMap() : new HashMap<>();
        log.debug("findCustomers: filterMap={}", filterMap);

        // ✅ Konvertiere die Sortierung in Map<String, String>
        Map<String, String> sortMap = order != null
            ? Map.of(order.field().name(), order.direction().name())
            : Map.of();
        log.debug("findCustomers: sortMap={}", sortMap);

        // ✅ Falls keine Paginierung angegeben → Alle Daten zurückgeben
        int page = pagination != null ? pagination.offset()-1 : 0;
        int size = pagination != null ? pagination.limit() : Integer.MAX_VALUE;
        log.debug("findCustomers: page={}, size={}", page, size);

        return customerReadService.find(filterMap, page, size, sortMap);
    }


    @QueryMapping("hallo")
    public String hello() {
        return "Hello, GraphQL!";
    }

    /**
     * Behandelt eine `AccessForbiddenException` und gibt ein entsprechendes GraphQL-Fehlerobjekt zurück.
     *
     * @param ex Die ausgelöste Ausnahme.
     * @param env Das GraphQL-Umfeld für Fehlerinformationen.
     * @return Ein `GraphQLError` mit der Fehlerbeschreibung.
     */
    @GraphQlExceptionHandler
    GraphQLError onAccessForbidden(final AccessForbiddenException ex, DataFetchingEnvironment env) {
        return GraphQLError.newError()
            .errorType(FORBIDDEN)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath().toList()) // Dynamischer Query-Pfad
            .location(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation()) // GraphQL Location
            .build();
    }

    /**
     * Behandelt eine `NotFoundException` und gibt ein entsprechendes GraphQL-Fehlerobjekt zurück.
     *
     * @param ex Die ausgelöste Ausnahme.
     * @param env Das GraphQL-Umfeld für Fehlerinformationen.
     * @return Ein `GraphQLError` mit der Fehlerbeschreibung.
     */
    @GraphQlExceptionHandler
    GraphQLError onNotFound(final NotFoundException ex, DataFetchingEnvironment env) {
        return GraphQLError.newError()
            .errorType(NOT_FOUND)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath().toList()) // Dynamischer Query-Pfad
            .location(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation()) // GraphQL Location
            .build();
    }
}

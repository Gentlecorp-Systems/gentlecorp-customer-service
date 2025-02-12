package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.EmailExistsException;
import com.gentlecorp.customer.exception.UnauthorizedException;
import com.gentlecorp.customer.exception.UsernameExistsException;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.input.FilterInput;
import com.gentlecorp.customer.model.input.PaginationInput;
import com.gentlecorp.customer.model.input.SortInput;
import com.gentlecorp.customer.service.CustomerReadService;
import com.gentlecorp.customer.service.JwtService;
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

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyMap;
import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;
import static org.springframework.graphql.execution.ErrorType.FORBIDDEN;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final CustomerReadService customerReadService;
    private final JwtService jwtService;

    Pair<String, String> validateJwtAndGetUsernameAndRole(Jwt jwt) {
        final var username = jwtService.getUsername(jwt);
        if (username == null) {
            throw new UnauthorizedException("Missing username in token");
        }

        final var role = jwtService.getRole(jwt);
        if (role == null) {
            throw new UnauthorizedException("Missing role in token");
        }

        return Pair.of(username, role);
    }


    @QueryMapping("customer")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    Customer getById(
        @Argument final UUID id,
        final Authentication authentication
    ) {
        log.debug("findById: id={}, user={}", id, authentication);

        final var user = (UserDetails) authentication.getPrincipal();
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

//    @GraphQlExceptionHandler
//    GraphQLError onEmailExists(final EmailExistsException ex) {
//        return GraphQLError.newError()
//            .errorType(BAD_REQUEST)
//            .message("Die Emailadresse " + ex.getEmail() + " existiert bereits.")
//            .path(List.of("input", "email")) // NOSONAR
//            .build();
//    }

//    @GraphQlExceptionHandler
//    GraphQLError onUsernameExists(final UsernameExistsException ex) {
//        final List<Object> path = List.of("input", "username");
//        return GraphQLError.newError()
//            .errorType(BAD_REQUEST)
//            .message("Der Username " + ex.getUsername() + " existiert bereits.")
//            .path(path)
//            .build();
//    }
//
//    @GraphQlExceptionHandler
//    GraphQLError onDateTimeParseException(final DateTimeParseException ex) {
//        final List<Object> path = List.of("input", "geburtsdatum");
//        return GraphQLError.newError()
//            .errorType(BAD_REQUEST)
//            .message("Das Datum " + ex.getParsedString() + " ist nicht korrekt.")
//            .path(path)
//            .build();
//    }
//
//    @GraphQlExceptionHandler
//    Collection<GraphQLError> onConstraintViolations(final ConstraintViolationException ex) {
//        return ex.getConstraintViolations()
//            .stream()
//            .map(this::violationToGraphQLError)
//            .toList();
//    }

    @GraphQlExceptionHandler
    GraphQLError onAccessForbidden(final AccessForbiddenException ex, DataFetchingEnvironment env) {
        return GraphQLError.newError()
            .errorType(FORBIDDEN)
            .message(ex.getMessage())
            .path(env.getExecutionStepInfo().getPath().toList()) // Dynamischer Query-Pfad
            .location(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation()) // GraphQL Location
            .build();
    }

    private GraphQLError violationToGraphQLError(final ConstraintViolation<?> violation) {
        // String oder Integer als Listenelement
        final List<Object> path = new ArrayList<>(List.of("input"));

        final var propertyPath = violation.getPropertyPath();
        StreamSupport.stream(propertyPath.spliterator(), false)
            .filter(node -> !node.getName().equals("create") && !node.getName().equals("kunde"))
            .forEach(node -> path.add(node.toString()));

        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message(violation.getMessage())
            .path(path)
            .build();
    }
}

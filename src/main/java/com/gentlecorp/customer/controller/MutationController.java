package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.ConstraintViolationsException;
import com.gentlecorp.customer.exception.EmailExistsException;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.UsernameExistsException;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import com.gentlecorp.customer.model.dto.CustomerUpdateDTO;
import com.gentlecorp.customer.model.dto.PasswordDTO;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.mapper.CustomerMapper;
import com.gentlecorp.customer.security.CustomUserDetails;
import com.gentlecorp.customer.service.CustomerWriteService;
import com.gentlecorp.customer.util.Validation;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.gentlecorp.customer.util.Validation.validateContact;
import static com.gentlecorp.customer.util.VersionUtils.getVersion;
import static com.gentlecorp.customer.util.VersionUtils.validateVersion;
import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;
import static org.springframework.graphql.execution.ErrorType.FORBIDDEN;
import static org.springframework.graphql.execution.ErrorType.NOT_FOUND;
import static org.springframework.http.ResponseEntity.noContent;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MutationController {

    private final CustomerWriteService customerWriteService;
    private final Validation validation;
    private final CustomerMapper customerMapper;

    @MutationMapping("createCustomer")
    Customer createCustomer(
        @Argument("input") final CustomerDTO customerDTO,
        @Argument("password") final String password
    ) {
        log.debug("createCustomer: customerDTO={}", customerDTO);
        validation.validateDTO(customerDTO);
        final var customerInput = customerMapper.toCustomer(customerDTO);
        final var customer = customerWriteService.create(customerInput, password);

        log.debug("createCustomer: customer={}", customer);
        return customer;
    }

    @MutationMapping("updateCustomer")
    Customer updateCustomer(
        @Argument("id") final UUID id,
        @Argument("version") final int version,
        @Argument("input") final CustomerUpdateDTO customerDTO,
        final Authentication authentication
    ) {
        log.debug("updateCustomer: id={}, customerDTO={}", id, customerDTO);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        validation.validateDTO(customerDTO);
        log.trace("updateCustomer: No constraints violated");

        final var customerInput = customerMapper.toCustomer(customerDTO);
        final var updatedCustomer = customerWriteService.update(customerInput, id, version, user);

        log.debug("updateCustomer: customer={}", updatedCustomer);
        return updatedCustomer;
    }

    @MutationMapping("addContact")
    UUID addContact(
        @Argument("id") final UUID id,
        @Argument("input") final ContactDTO contactDTO,
        final Authentication authentication
    ) {
        log.debug("addContact: id={}, contactDTO={}", id, contactDTO);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        validation.validateDTO(contactDTO);
        log.trace("addContact: No constraints violated");

        final var customerInput = customerMapper.toContact(contactDTO);
        final var newContactId = customerWriteService.addContact(id, customerInput, user);

        log.debug("addContact: newContactId={}", newContactId);
        return newContactId;
    }

    @MutationMapping("updateContact")
    Contact updateContact(
        @Argument("id") final UUID id,
        @Argument("customerVersion") final int customerVersion,
        @Argument("contactId") final UUID contactId,
        @Argument("contactVersion") final int contactVersion,
        @Argument("input") final ContactDTO contactDTO,
        final Authentication authentication
    ) {
        log.debug("addContact: id={}, contactDTO={}", id, contactDTO);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        validation.validateDTO(contactDTO);
        log.trace("addContact: No constraints violated");

        final var customerInput = customerMapper.toContact(contactDTO);
        final var newContact = customerWriteService.updateContact(id, customerVersion, contactId, contactVersion, customerInput, user);

        log.debug("addContact: newContact={}", newContact);
        return newContact;
    }

    @MutationMapping("removeContact")
    boolean removeContact(
        @Argument("id") final UUID id,
        @Argument("customerVersion") final int customerVersion,
        @Argument("contactId") final UUID contactId,
        @Argument("contactVersion") final int contactVersion,
        final Authentication authentication
    ) {
        log.debug("addContact: id={}", id);
        final var user = (CustomUserDetails) authentication.getPrincipal();

        final var success = customerWriteService.removeContact(id, customerVersion, contactId, contactVersion, user);

        return success;
    }

    @MutationMapping("updatePassword")
    void updatePassword(
        @Argument("newPassword") final String newPassword,
        final Authentication authentication
    ) {
        log.debug("updatePassword: newPassword={}", newPassword);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        customerWriteService.updatePassword(newPassword, user);
    }

    @MutationMapping("deleteCustomer")
    void deleteCustomer(
        @Argument final UUID id,
        @Argument final int version,
        final Authentication authentication
    ) {
        log.debug("deleteCustomer: id={}, version={}", id, version);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        customerWriteService.deleteById(id,version, user);
    }


    @GraphQlExceptionHandler
    GraphQLError onEmailExists(final EmailExistsException ex) {
        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message("Die Emailadresse " + ex.getEmail() + " existiert bereits.")
            .path(List.of("input", "email")) // NOSONAR
            .build();
    }

    @GraphQlExceptionHandler
    GraphQLError onUsernameExists(final UsernameExistsException ex) {
        final List<Object> path = List.of("input", "username");
        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message("Der Username " + ex.getUsername() + " existiert bereits.")
            .path(path)
            .build();
    }

    @GraphQlExceptionHandler
    GraphQLError onDateTimeParseException(final DateTimeParseException ex) {
        final List<Object> path = List.of("input", "geburtsdatum");
        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message("Das Datum " + ex.getParsedString() + " ist nicht korrekt.")
            .path(path)
            .build();
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

    @GraphQlExceptionHandler
    Collection<GraphQLError> onConstraintViolations(final ConstraintViolationsException ex, DataFetchingEnvironment env) {
        return  Stream.concat(
                ex.getCustomerViolations().stream(),
                ex.getContactViolations().stream()
            )
            .map(violations -> violationToGraphQLError(violations, env))
            .toList();
    }

    private GraphQLError violationToGraphQLError(final ConstraintViolation<?> violation, DataFetchingEnvironment env) {
        // String oder Integer als Listenelement
        final List<Object> path = new ArrayList<>(List.of("input"));

        final var propertyPath = violation.getPropertyPath();
        StreamSupport.stream(propertyPath.spliterator(), false)
            .filter(node -> !Set.of("create", "customer", "contact").contains(node.getName()))
            .forEach(node -> path.add(node.toString()));

        SourceLocation location = Optional.ofNullable(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation())
            .orElse(new SourceLocation(0, 0));  // Fallback auf Zeile 0, Spalte 0

        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message(violation.getMessage())
            .location(location)
            .path(path)
            .build();
    }
}

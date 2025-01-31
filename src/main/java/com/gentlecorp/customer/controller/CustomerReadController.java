package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.model.BaseCustomerModel;
import com.gentlecorp.customer.model.CustomerModel;
import com.gentlecorp.customer.model.FullCustomerModel;
import com.gentlecorp.customer.service.CustomerReadService;
import com.gentlecorp.customer.util.ControllerUtils;
import com.gentlecorp.customer.util.UriHelper;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static com.gentlecorp.customer.util.Constants.ID_PATTERN;
import static com.gentlecorp.customer.util.ControllerUtils.createETag;
import static com.gentlecorp.customer.util.ControllerUtils.isETagMatching;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(CUSTOMER_PATH)
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "v2"))
@RequiredArgsConstructor
@Slf4j
public class CustomerReadController {

    private final CustomerReadService customerReadService;
    private final UriHelper uriHelper;

    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    @Observed(name = "get-by-id")
    @Operation(summary = "Search for a customer by ID", tags = "Search")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<CustomerModel> getById(
        @PathVariable final UUID id,
        @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) final String version,
        final HttpServletRequest request,
        @AuthenticationPrincipal final Jwt jwt
    ) {
        log.info("getById: id={}, if-none-match={}", id,version);
        final var customer = customerReadService.findById(id, jwt, false);
        final var currentVersion = createETag(customer.getVersion());

        if (isETagMatching(Optional.ofNullable(version), currentVersion)) {
            return ResponseEntity.status(NOT_MODIFIED).build();
        }

        final var model = new CustomerModel(customer);
        addLinksToModel(model, request, customer.getId());
        return ok().eTag(currentVersion).body(model);
    }

    @GetMapping(produces = HAL_JSON_VALUE)
    @Operation(summary = "Search for customers using criteria", tags = "Search")
    @ApiResponse(responseCode = "200", description = "CollectionModel with customers found")
    @ApiResponse(responseCode = "404", description = "No customers found")
    public CollectionModel<CustomerModel> get(
        @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
        final HttpServletRequest request
    ) {
        String sanitizedSearchCriteria = searchCriteria.toString().replace("\n", "").replace("\r", "");
        log.debug("get: searchCriteria={}", sanitizedSearchCriteria);
        final var baseUri = uriHelper.getBaseUri(request).toString();

        final var models = customerReadService.find(searchCriteria)
            .stream()
            .map(customer -> {
                final var model = new CustomerModel(customer);
                model.add(Link.of(String.format("%s/%s", baseUri, customer.getId())));
                return model;
            })
            .toList();

        log.debug("get: models={}", models);
        return CollectionModel.of(models);
    }

    private void addLinksToModel(BaseCustomerModel<?> model, HttpServletRequest request, UUID id) {
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = String.format("%s/%s", baseUri, id);

        model.add(
            Link.of(idUri).withSelfRel(),
            Link.of(baseUri).withRel("list"),
            Link.of(baseUri).withRel("add"),
            Link.of(idUri).withRel("update"),
            Link.of(idUri).withRel("remove")
        );
    }

    @GetMapping(path = "/hallo")
    private ResponseEntity<String> Hallo() {
        log.debug("Hallo");
        return ResponseEntity.ok("Hallo");
    }
}

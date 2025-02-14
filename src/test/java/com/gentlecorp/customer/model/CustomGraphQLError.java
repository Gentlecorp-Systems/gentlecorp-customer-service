package com.gentlecorp.customer.model;

import graphql.ErrorClassification;
import graphql.language.SourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import graphql.GraphQLError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomGraphQLError implements GraphQLError {

    private static final Logger log = LoggerFactory.getLogger(CustomGraphQLError.class);
    private final String message;
    private final List<SourceLocation> locations;
    private final ErrorClassification errorType;
    private final Map<String, Object> extensions;

    public CustomGraphQLError(String message, List<SourceLocation> locations, ErrorClassification errorType, Map<String, Object> extensions) {
        this.message = message;
        this.locations = locations;
        this.errorType = errorType;
        this.extensions = extensions;
    }

    public CustomGraphQLError(String message) {
        this.message = message;
        this.locations = new ArrayList<>();
        this.errorType = null;
        this.extensions = null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return locations;
    }

    @Override
    public ErrorClassification getErrorType() {
        return errorType;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public Map<String, Object> toSpecification() {
        return Map.of(
            "message", message,
            "locations", locations != null ? locations : List.of(),
            "extensions", extensions != null ? extensions : Map.of()
        );
    }
}

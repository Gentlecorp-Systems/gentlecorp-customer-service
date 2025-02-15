package com.gentlecorp.customer.model;

import graphql.GraphQLError;

import java.util.List;

public class GraphQlResponse<T> {
    private final T data;
    private final List<CustomGraphQLError> errors;

    public GraphQlResponse(T data, List<CustomGraphQLError> errors) {
        this.data = data;
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public T getData() {
        return data;
    }

    public List<CustomGraphQLError> getErrors() {
        return errors;
    }
}

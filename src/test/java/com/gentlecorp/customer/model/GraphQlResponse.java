package com.gentlecorp.customer.model;

import graphql.GraphQLError;

import java.util.List;

public class GraphQlResponse<T> {
    private final T data;
    private final List<GraphQLError> errors;

    public GraphQlResponse(T data, List<GraphQLError> errors) {
        this.data = data;
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public T getData() {
        return data;
    }

    public List<GraphQLError> getErrors() {
        return errors;
    }
}

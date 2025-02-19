package com.gentlecorp.customer.model.payload;

import com.gentlecorp.customer.model.entity.Customer;

import java.util.List;

public record MutationResponse(
    boolean success,
    String message,
    Customer result,
    int affectedCount,
    List<String> warnings
) {
}

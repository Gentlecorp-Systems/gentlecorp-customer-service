package com.gentlecorp.customer.model.dto;

import java.util.UUID;

public record ShoppingCartDTO2(
  UUID customerId,
  String token
) {
}

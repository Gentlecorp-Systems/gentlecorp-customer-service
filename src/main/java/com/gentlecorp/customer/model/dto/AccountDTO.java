package com.gentlecorp.customer.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDTO(
  BigDecimal balance,
  String category,
  int rateOfInterest,
  int overdraft,
  int withdrawalLimit,
  UUID customerId
) {
}

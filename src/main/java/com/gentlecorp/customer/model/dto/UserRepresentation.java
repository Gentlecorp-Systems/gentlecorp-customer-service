package com.gentlecorp.customer.model.dto;

public record UserRepresentation(
  String id,
  String username,
  String email,
  String firstName,
  String lastName
) {
}

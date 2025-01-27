package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum InterestType {
    INVESTMENTS("I"),
    SAVING_AND_FINANCE("SF"),
    CREDIT_AND_DEBT("CD"),
    BANK_PRODUCTS_AND_SERVICES("BPS"),
    FINANCIAL_EDUCATION_AND_COUNSELING("FEC"),
    REAL_ESTATE("RE"),
    INSURANCE("IN"),
    SUSTAINABLE_FINANCE("SUF"),
    TECHNOLOGY_AND_INNOVATION("IT"),
    TRAVEL("T");

    private final String interest;


    @JsonCreator
    public static InterestType of(final String value) {
        return Stream.of(values())
                .filter(interestType -> interestType.interest.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }


  @JsonValue
  public String getInterest() {
    return interest;
  }
}

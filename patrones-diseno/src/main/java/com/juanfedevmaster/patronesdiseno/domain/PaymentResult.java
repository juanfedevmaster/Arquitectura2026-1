package com.juanfedevmaster.patronesdiseno.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class PaymentResult {
    private final boolean success;
    private final String message;
    private final String transactionId;
    private final double chargedAmount;
}

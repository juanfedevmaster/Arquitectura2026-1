package com.juanfedevmaster.patronesdiseno.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Payment {
    private final String id;
    private double amount;
    private final double originalAmount;
    private final String method;
    private final String currency;
    private String status;
}

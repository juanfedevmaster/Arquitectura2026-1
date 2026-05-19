package com.juanfedevmaster.patronesdiseno.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {

    private final String id;
    private double amount;
    private final double originalAmount;
    private final String method;
    private final String currency;
    private String status;   
    private double savingAmount; 

    public Payment(String id, double amount, String method, String currency) {
        this.id = id;
        this.amount = amount;
        this.originalAmount = amount;
        this.method = method;
        this.currency = currency;
        this.status = "PENDING";
        this.savingAmount = 0.0;
    }
}

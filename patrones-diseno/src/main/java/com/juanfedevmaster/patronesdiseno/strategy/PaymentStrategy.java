package com.juanfedevmaster.patronesdiseno.strategy;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public interface PaymentStrategy {
    PaymentResult payProcess(Payment payment);

    String getMethodName();
}

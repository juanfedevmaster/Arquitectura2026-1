package com.juanfedevmaster.patronesdiseno.decorator;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public interface PaymentProcessor {
    PaymentResult process(Payment payment);
}

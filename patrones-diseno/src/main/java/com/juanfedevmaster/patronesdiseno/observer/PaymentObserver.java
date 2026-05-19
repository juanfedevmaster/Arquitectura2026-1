package com.juanfedevmaster.patronesdiseno.observer;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public interface PaymentObserver {
    void onPaymentProcessed(Payment payment, PaymentResult result);
}

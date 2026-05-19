package com.juanfedevmaster.patronesdiseno.observer;

import java.util.List;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public class PaymentEventPublisher {
    private final List<PaymentObserver> observers;

    public PaymentEventPublisher(List<PaymentObserver> observers) {
        this.observers = observers;
    }

    public void publish(Payment payment, PaymentResult result) {
        observers.forEach(observer -> observer.onPaymentProcessed(payment, result));
    }
}

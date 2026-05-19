package com.juanfedevmaster.patronesdiseno.decorator;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;
import com.juanfedevmaster.patronesdiseno.strategy.PaymentStrategy;

public class BasePaymentProcessor implements PaymentProcessor {

    private final PaymentStrategy strategy;

    public BasePaymentProcessor(PaymentStrategy strategy){
        this.strategy = strategy;
    }

    @Override
    public PaymentResult process(Payment payment) {
        return strategy.payProcess(payment);
    }

}

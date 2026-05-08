package com.juanfedevmaster.patronesdiseno.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

@Component
public class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult payProcess(Payment payment) {
        String txId = "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String msg = String.format("Pago PayPal completado. Se debitaron $%.2f %s de tu cuenta.",
                payment.getAmount(), payment.getCurrency());
        return new PaymentResult(true, msg, txId, payment.getAmount());
    }

    @Override
    public String getMethodName() {
        return "PAYPAL";
    }
}

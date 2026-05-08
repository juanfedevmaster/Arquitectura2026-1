package com.juanfedevmaster.patronesdiseno.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

@Component
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult payProcess(Payment payment) {
        String txId   = "CRYPTO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String txHash = "0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String msg = String.format(
                "Pago cripto confirmado. Monto: $%.2f %s. Hash de transacción: %s",
                payment.getAmount(), payment.getCurrency(), txHash);
        return new PaymentResult(true, msg, txId, payment.getAmount());
    }

    @Override
    public String getMethodName() {
        return "CRYPTO";
    }
}

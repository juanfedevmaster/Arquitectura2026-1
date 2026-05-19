package com.juanfedevmaster.patronesdiseno.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public class AuditLogObserver implements PaymentObserver{
    private static final Logger log = LoggerFactory.getLogger(AuditLogObserver.class);

    @Override
    public void onPaymentProcessed(Payment payment, PaymentResult result) {
        log.info("[AUDIT] PagoID={} | Método={} | Moneda={} | MontoOriginal=${} | " +
                 "MontoCobrado=${} | Estado={} | TX={}",
                payment.getId(), payment.getMethod(), payment.getCurrency(),
                payment.getOriginalAmount(), result.getChargedAmount(),
                result.isSuccess() ? "SUCCESS" : "FAILED",
                result.getTransactionId());
    }
}

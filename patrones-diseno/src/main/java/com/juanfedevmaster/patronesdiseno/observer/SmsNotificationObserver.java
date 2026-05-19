package com.juanfedevmaster.patronesdiseno.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public class SmsNotificationObserver implements PaymentObserver{

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationObserver.class);


    @Override
    public void onPaymentProcessed(Payment payment, PaymentResult result) {
        String estado = result.isSuccess() ? "aprobado ✓" : "rechazado ✗";
        log.info("[SMS] Tu pago de ${} con {} fue {}. TX: {}",
                result.getChargedAmount(), payment.getMethod(),
                estado, result.getTransactionId());
    }

}

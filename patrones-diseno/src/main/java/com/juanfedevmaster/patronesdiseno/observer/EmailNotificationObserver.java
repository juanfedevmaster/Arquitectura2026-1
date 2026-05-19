package com.juanfedevmaster.patronesdiseno.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public class EmailNotificationObserver implements PaymentObserver{
    
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void onPaymentProcessed(Payment payment, PaymentResult result) {
        if (result.isSuccess()) {
            log.info("[EMAIL] Confirmación enviada al cliente. | PagoID={} | Monto=${}  | Método={}",
                    payment.getId(), result.getChargedAmount(), payment.getMethod());
        } else {
            log.warn("[EMAIL] Notificación de pago fallido. | PagoID={} | Detalle={}",
                    payment.getId(), result.getMessage());
        }
    }
}

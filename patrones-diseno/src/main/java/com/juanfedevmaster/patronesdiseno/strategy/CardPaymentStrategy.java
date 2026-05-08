package com.juanfedevmaster.patronesdiseno.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

@Component
public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult payProcess(Payment payment){
        String txId = "CARD-"+UUID.randomUUID().toString().substring(0,8);
        String msg = String.format("Pago con tarjeta aprobado. Cargo de $%.2f %s.",
            payment.getAmount(), payment.getCurrency());

        return new PaymentResult(true,msg, txId, payment.getAmount());
    }

    @Override
    public String getMethodName(){
        return "CARD";
    }
}

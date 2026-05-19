package com.juanfedevmaster.patronesdiseno.decorator;

import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;

public class DiscountDecorator implements PaymentProcessor{

    private final PaymentProcessor wrapped;
    private final double discountPercentage;
    private final String discountCode;

    public DiscountDecorator(PaymentProcessor wrapped, double discountPercentage, String discountCode) {
        this.wrapped = wrapped;
        this.discountPercentage = discountPercentage;
        this.discountCode = discountCode;
    }

    @Override
    public PaymentResult process(Payment payment) {
        double savings = payment.getAmount() * (discountPercentage / 100.0);
        double discountedAmount = payment.getAmount() - savings;

        // Modificamos el monto en el dominio antes de procesar
        payment.setAmount(discountedAmount);

        // Monto ahorrado.
        payment.setSavingAmount(savings);

        PaymentResult base = wrapped.process(payment);

        String enrichedMessage = String.format(
                "%s [Descuento '%s' (%.0f%%) aplicado: -$%.2f]",
                base.getMessage(), discountCode, discountPercentage, savings);

        return new PaymentResult(base.isSuccess(), enrichedMessage,
                base.getTransactionId(), discountedAmount);

    }
}

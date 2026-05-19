package com.juanfedevmaster.patronesdiseno.service;

import java.util.Optional;
import java.util.UUID;

import com.juanfedevmaster.patronesdiseno.decorator.BasePaymentProcessor;
import com.juanfedevmaster.patronesdiseno.decorator.DiscountDecorator;
import com.juanfedevmaster.patronesdiseno.decorator.PaymentProcessor;
import com.juanfedevmaster.patronesdiseno.domain.Payment;
import com.juanfedevmaster.patronesdiseno.domain.PaymentResult;
import com.juanfedevmaster.patronesdiseno.dto.PaymentRequestDTO;
import com.juanfedevmaster.patronesdiseno.dto.PaymentResponseDTO;
import com.juanfedevmaster.patronesdiseno.factory.PaymentStrategyFactory;
import com.juanfedevmaster.patronesdiseno.observer.PaymentEventPublisher;
import com.juanfedevmaster.patronesdiseno.strategy.PaymentStrategy;

public class PaymentService {
    private final PaymentStrategyFactory strategyFactory;
    private final PaymentEventPublisher eventPublisher;
    private final DiscountService discountService;

    public PaymentService(PaymentStrategyFactory strategyFactory,
            PaymentEventPublisher eventPublisher,
            DiscountService discountService) {
        this.strategyFactory = strategyFactory;
        this.eventPublisher = eventPublisher;
        this.discountService = discountService;
    }

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // 1. DTO → Dominio
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                request.getAmount(),
                request.getMethod().toUpperCase(),
                request.getCurrency() != null ? request.getCurrency().toUpperCase() : "USD");

        // 2. Factory Method: obtener la estrategia correcta
        PaymentStrategy strategy = strategyFactory.getStrategy(request.getMethod());

        // 3. Decorator: envolver con descuento si se proporcionó un código válido
        PaymentProcessor processor = new BasePaymentProcessor(strategy);
        Optional<Double> discount = discountService.getDiscount(request.getDiscountCode());
        if (discount.isPresent()) {
            processor = new DiscountDecorator(processor, discount.get(), request.getDiscountCode());
        }

        // 4. Procesar el pago
        PaymentResult result = processor.process(payment);
        payment.setStatus(result.isSuccess() ? "COMPLETED" : "FAILED");

        // 5. Observer: notificar a todos los suscriptores
        eventPublisher.publish(payment, result);

        // 6. Dominio → DTO de respuesta
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOriginalAmount(),
                result.getChargedAmount(),
                payment.getMethod(),
                payment.getStatus(),
                result.getMessage(),
                result.getTransactionId(),
                discount.orElse(0.0));
    }
}

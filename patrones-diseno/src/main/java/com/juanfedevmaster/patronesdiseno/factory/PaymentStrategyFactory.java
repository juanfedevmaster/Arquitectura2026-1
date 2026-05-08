package com.juanfedevmaster.patronesdiseno.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.juanfedevmaster.patronesdiseno.strategy.PaymentStrategy;

public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies){
        this.strategies = strategies.stream()
            .collect(Collectors.toMap(
                s -> s.getMethodName().toUpperCase(),
                Function.identity()
            ));
    }

    public PaymentStrategy getStrategy(String method){
        PaymentStrategy strategy = strategies.get(method.toUpperCase());
        if(strategy == null){
            throw new IllegalArgumentException(
                "Método de pago no soportado: " + method + "Metodos disponibles: "+strategies.keySet()
            );
        }
        return strategy;
    }
}

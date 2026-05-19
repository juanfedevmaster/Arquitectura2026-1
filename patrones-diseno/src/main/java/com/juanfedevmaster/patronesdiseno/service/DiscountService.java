package com.juanfedevmaster.patronesdiseno.service;

import java.util.Map;
import java.util.Optional;

public class DiscountService {
    private static final Map<String, Double> DISCOUNT_CODES = Map.of(
            "SAVE10",   10.0,
            "SAVE20",   20.0,
            "SAVE50",   50.0,
            "CRYPTO15", 15.0,
            "WELCOME5",  5.0
    );

    public Optional<Double> getDiscount(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(DISCOUNT_CODES.get(code.trim().toUpperCase()));
    }

    public Map<String, Double> getAvailableCodes() {
        return Map.copyOf(DISCOUNT_CODES);
    }
}

package com.juanfedevmaster.patronesdiseno.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    /**
     * GET /api/payments
     * get the supported methods in my web application. 
     * @return String[]
     */
    @GetMapping("/methods")
    public ResponseEntity<String[]> getSupportedMethods() {
        return ResponseEntity.ok(new String[] { "CARD", "PAYPAL", "CRYPTO" });
    }

    /**
     * GET /api/payments/discounts
      get the available discounts in my web application.
     * @return
     */
    @GetMapping("/discounts")
    public ResponseEntity<Map<String, Double>> getAvailableDiscouts() {

        return ResponseEntity.ok(Map.of(
            "SUMMER_SALE", 0.15,
            "BLACK_FRIDAY", 0.25,
            "CYBER_MONDAY", 0.20
        ));
    }

    @PostMapping("/payment")
    public String processPayment() {
        return new String();
    }

}

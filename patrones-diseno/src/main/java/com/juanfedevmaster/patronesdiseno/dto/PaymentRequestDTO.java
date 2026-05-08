package com.juanfedevmaster.patronesdiseno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO - The responsability of separate the models on domain of the API layer.
 */
public class PaymentRequestDTO {

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double amount;

    @NotBlank(message = "El método de pago es obligatorio (CARD, PAYPAL, CRYPTO)")
    private String method;

    private String currency;       // optional, default USD

    private String discountCode;   // optional

    public PaymentRequestDTO() {}

    public Double getAmount()              { return amount; }
    public void setAmount(Double amount)   { this.amount = amount; }

    public String getMethod()              { return method; }
    public void setMethod(String method)   { this.method = method; }

    public String getCurrency()            { return currency; }
    public void setCurrency(String c)      { this.currency = c; }

    public String getDiscountCode()        { return discountCode; }
    public void setDiscountCode(String d)  { this.discountCode = d; }
}


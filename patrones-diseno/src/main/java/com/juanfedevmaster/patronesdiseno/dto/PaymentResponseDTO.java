package com.juanfedevmaster.patronesdiseno.dto;

public class PaymentResponseDTO {

    private final String paymentId;
    private final double originalAmount;
    private final double finalAmount;
    private final double discountPercentage;
    private final String method;
    private final String status;
    private final String message;
    private final String transactionId;

    public PaymentResponseDTO(String paymentId, double originalAmount, double finalAmount,
                              String method, String status, String message,
                              String transactionId, double discountPercentage) {
        this.paymentId = paymentId;
        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
        this.method = method;
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
        this.discountPercentage = discountPercentage;
    }

    public String getPaymentId()          { return paymentId; }
    public double getOriginalAmount()     { return originalAmount; }
    public double getFinalAmount()        { return finalAmount; }
    public double getDiscountPercentage() { return discountPercentage; }
    public String getMethod()             { return method; }
    public String getStatus()             { return status; }
    public String getMessage()            { return message; }
    public String getTransactionId()      { return transactionId; }
}

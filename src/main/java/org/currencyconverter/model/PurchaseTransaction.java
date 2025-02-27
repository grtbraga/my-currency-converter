package org.currencyconverter.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class PurchaseTransaction {
    private int id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal purchaseAmountUSD;

    // validation and rounding
    public PurchaseTransaction(int id, String description, LocalDate transactionDate, BigDecimal purchaseAmountUSD) {
        if (description == null || description.trim().isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("Description must be non-null, non-empty, and not exceed 50 characters");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date must not be null");
        }
        if (purchaseAmountUSD == null || purchaseAmountUSD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase amount must be positive");
        }

        this.id = id;
        this.description = description;
        this.transactionDate = transactionDate;
        this.purchaseAmountUSD = purchaseAmountUSD.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public BigDecimal getPurchaseAmountUSD() { return purchaseAmountUSD; }
}
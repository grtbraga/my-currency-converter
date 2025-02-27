package org.currencyconverter.controller;

import org.currencyconverter.model.PurchaseTransaction;
import org.currencyconverter.service.CurrencyApiService;
import org.currencyconverter.service.CurrencyConverterService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PurchaseController {
    private final Map<Integer, PurchaseTransaction> transactionStorage = new HashMap<Integer, PurchaseTransaction>();
    private final CurrencyConverterService currencyConverterService = new CurrencyConverterService();
    private final CurrencyApiService currencyApiService = new CurrencyApiService();

    public void addTransaction(int id, String description, LocalDate date, BigDecimal amountUSD) {
        PurchaseTransaction transaction = new PurchaseTransaction(id, description, date, amountUSD);
        transactionStorage.put(id, transaction);
        System.out.println("Transaction added: " + id);
    }

    public String convertTransaction(String id, String targetCurrency) throws Exception {
        PurchaseTransaction transaction = transactionStorage.get(id);
        if (transaction == null) {
            return "Transaction not found.";
        }

        BigDecimal rate = currencyApiService.fetchExchangeRate(targetCurrency, transaction.getTransactionDate());
        BigDecimal convertedAmount = currencyConverterService.convertToTargetCurrency(transaction.getPurchaseAmountUSD(), rate);

        return String.format("Transaction %s: %s converted to %s = %.2f",
                id, transaction.getPurchaseAmountUSD(), targetCurrency, convertedAmount);
    }
}
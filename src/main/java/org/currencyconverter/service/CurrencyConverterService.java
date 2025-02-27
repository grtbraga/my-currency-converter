package org.currencyconverter.service;

import java.math.BigDecimal;

public class CurrencyConverterService {

    public BigDecimal convertToTargetCurrency(BigDecimal amountUSD, BigDecimal exchangeRate) {
        if (amountUSD == null || exchangeRate == null) {
            throw new IllegalArgumentException("Amount and exchange rate must not be null");
        }
        if (amountUSD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }

        return amountUSD.multiply(exchangeRate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
package org.currencyconverter.service;

import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class CurrencyConverterServiceTest {

    private final CurrencyConverterService converter = new CurrencyConverterService();

    @Test
    public void testValidConversion() {
        BigDecimal result = converter.convertToTargetCurrency(BigDecimal.valueOf(100.00), BigDecimal.valueOf(1.15));
        assertEquals(BigDecimal.valueOf(115.00).setScale(2), result);
    }

    @Test
    public void testRoundingToTwoDecimals() {
        BigDecimal result = converter.convertToTargetCurrency(BigDecimal.valueOf(100.555), BigDecimal.valueOf(1.123));
        assertEquals(BigDecimal.valueOf(112.92).setScale(2), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAmount() {
        converter.convertToTargetCurrency(null, BigDecimal.valueOf(1.15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRate() {
        converter.convertToTargetCurrency(BigDecimal.valueOf(100.00), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeAmount() {
        converter.convertToTargetCurrency(BigDecimal.valueOf(-100.00), BigDecimal.valueOf(1.15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRate() {
        converter.convertToTargetCurrency(BigDecimal.valueOf(100.00), BigDecimal.valueOf(-1.15));
    }
}
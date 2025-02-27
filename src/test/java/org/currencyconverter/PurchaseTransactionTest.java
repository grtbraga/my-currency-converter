package org.currencyconverter.model;

import org.junit.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class PurchaseTransactionTest {

    @Test
    public void testValidTransaction() {
        PurchaseTransaction tx = new PurchaseTransaction(1, "Test", LocalDate.of(2025, 2, 26), BigDecimal.valueOf(10.50));
        assertEquals(1, tx.getId());
        assertEquals("Test", tx.getDescription());
        assertEquals(LocalDate.of(2025, 2, 26), tx.getTransactionDate());
        assertEquals(BigDecimal.valueOf(10.50).setScale(2), tx.getPurchaseAmountUSD());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testDescriptionTooLong() {
        String longDesc = "A".repeat(51);
        new PurchaseTransaction(1, longDesc, LocalDate.now(), BigDecimal.valueOf(10.50));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDescription() {
        new PurchaseTransaction(1, null, LocalDate.now(), BigDecimal.valueOf(10.50));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDate() {
        new PurchaseTransaction(1, "Teste", null, BigDecimal.valueOf(10.50));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeAmount() {
        new PurchaseTransaction(1, "Teste", LocalDate.now(), BigDecimal.valueOf(-10.50));
    }

    @Test
    public void testAmountRoundedToCents() {
        PurchaseTransaction tx = new PurchaseTransaction(1, "Teste", LocalDate.now(), BigDecimal.valueOf(10.555));
        assertEquals(BigDecimal.valueOf(10.56).setScale(2), tx.getPurchaseAmountUSD());
    }
}
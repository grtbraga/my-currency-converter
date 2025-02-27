package org.currencyconverter;

import org.currencyconverter.model.PurchaseTransaction;
import org.currencyconverter.service.CurrencyApiService;
import org.currencyconverter.service.CurrencyConverterService;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CurrencyConverterServiceAppTest {

    private CurrencyApiService currencyApiServiceMock;
    private CurrencyConverterService currencyConverterServiceMock;
    private Map<Integer, PurchaseTransaction> transactions;

    @Before
    public void setUp() {
        currencyApiServiceMock = mock(CurrencyApiService.class);
        currencyConverterServiceMock = mock(CurrencyConverterService.class);
        transactions = new HashMap<>();
        CurrencyConverterApp.getTransactions().clear();
        CurrencyConverterApp.setNextId(1);
    }

    @Test
    public void testStoreAndRetrievePurchase() {
        int currentId = CurrencyConverterApp.getNextId();
        PurchaseTransaction tx = new PurchaseTransaction(currentId, "Teste", LocalDate.of(2025, 2, 26), BigDecimal.valueOf(100.00));
        transactions.put(tx.getId(), tx);
        CurrencyConverterApp.setNextId(currentId + 1);

        assertEquals(1, transactions.size());
        assertEquals(tx, transactions.get(1));
    }

    @Test
    public void testRetrieveAndConvert() throws Exception {
        when(currencyApiServiceMock.fetchExchangeRate(eq("Euro"), eq(LocalDate.of(2025, 2, 26))))
                .thenReturn(BigDecimal.valueOf(1.15));
        when(currencyConverterServiceMock.convertToTargetCurrency(any(BigDecimal.class), eq(BigDecimal.valueOf(1.15))))
                .thenReturn(BigDecimal.valueOf(115.00));

        int currentId = CurrencyConverterApp.getNextId();
        PurchaseTransaction tx = new PurchaseTransaction(currentId, "Teste", LocalDate.of(2025, 2, 26), BigDecimal.valueOf(100.00));
        transactions.put(tx.getId(), tx);
        CurrencyConverterApp.setNextId(currentId + 1);

        PurchaseTransaction retrieved = transactions.get(1);
        BigDecimal rate = currencyApiServiceMock.fetchExchangeRate("Euro", retrieved.getTransactionDate());
        BigDecimal converted = currencyConverterServiceMock.convertToTargetCurrency(retrieved.getPurchaseAmountUSD(), rate);

        assertEquals(BigDecimal.valueOf(115.00), converted);
    }
}
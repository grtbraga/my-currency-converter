package org.currencyconverter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class CurrencyApiServiceTest {

    private CurrencyApiService service;
    private HttpClient httpClientMock;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        // Setup mocks and response
        MockitoAnnotations.openMocks(this);

        httpClientMock = Mockito.mock(HttpClient.class);
        objectMapper = new ObjectMapper();
        service = new CurrencyApiService(httpClientMock, objectMapper);

        HttpResponse<String> defaultResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(defaultResponse.statusCode()).thenReturn(200);
        Mockito.when(defaultResponse.body()).thenReturn("{\"data\": []}");
        Mockito.when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(defaultResponse);
    }

    @Test
    public void testFetchExchangeRateWithinSixMonths() throws Exception {
        String jsonResponse = "{\"data\": [" +
                "{\"record_date\": \"2025-01-31\", \"country_currency_desc\": \"Brazil-Real\", \"exchange_rate\": \"5.40\"}," +
                "{\"record_date\": \"2025-01-31\", \"country_currency_desc\": \"United Kingdom-Pound\", \"exchange_rate\": \"0.80\"}" +
                "]}";
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn(jsonResponse);
        Mockito.when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        assertEquals(BigDecimal.valueOf(5.40).setScale(2), service.fetchExchangeRate("BRL", LocalDate.of(2025, 2, 26)));
        assertEquals(BigDecimal.valueOf(5.40).setScale(2), service.fetchExchangeRate("real", LocalDate.of(2025, 2, 26)));
        assertEquals(BigDecimal.valueOf(5.40).setScale(2), service.fetchExchangeRate("Brazilian real", LocalDate.of(2025, 2, 26)));
        assertEquals(BigDecimal.valueOf(0.80).setScale(2), service.fetchExchangeRate("GBP", LocalDate.of(2025, 2, 26)));
        assertEquals(BigDecimal.valueOf(0.80).setScale(2), service.fetchExchangeRate("pound", LocalDate.of(2025, 2, 26)));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoRateWithinSixMonths() throws Exception {
        String jsonResponse = "{\"data\": [{\"record_date\": \"2024-07-31\", \"country_currency_desc\": \"Euro Member Countries-Euro\", \"exchange_rate\": \"1.05\"}]}";
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn(jsonResponse);
        Mockito.when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        service.fetchExchangeRate("Euro", LocalDate.of(2025, 2, 26));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCurrency() throws Exception {
        service.fetchExchangeRate(null, LocalDate.now());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDate() throws Exception {
        service.fetchExchangeRate("Euro", null);
    }
}
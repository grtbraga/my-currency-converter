package org.currencyconverter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CurrencyApiService {
    private static final String BASE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";
    private static final Map<String, String> CURRENCY_MAPPING = new HashMap<>();
    private static final Map<String, String> NAME_TO_CODE = new HashMap<>();

    static {
        // ISO 4217 codes mapped to country_currency_desc
        CURRENCY_MAPPING.put("BRL", "Brazil-Real");
        CURRENCY_MAPPING.put("CAD", "Canada-Dollar");
        CURRENCY_MAPPING.put("USD", "United States-Dollar");
        CURRENCY_MAPPING.put("MXN", "Mexico-Peso");
        CURRENCY_MAPPING.put("KYD", "Cayman Islands-Dollar");
        CURRENCY_MAPPING.put("ARS", "Argentina-Peso");
        CURRENCY_MAPPING.put("GBP", "United Kingdom-Pound");
        CURRENCY_MAPPING.put("EUR", "Euro Zone-Euro");
        CURRENCY_MAPPING.put("JPY", "Japan-Yen");
        CURRENCY_MAPPING.put("AUD", "Australia-Dollar");

        // mapped to ISO 4217 codes
        NAME_TO_CODE.put("real", "BRL");
        NAME_TO_CODE.put("brazilian real", "BRL");
        NAME_TO_CODE.put("dollar", "USD");
        NAME_TO_CODE.put("canadian dollar", "CAD");
        NAME_TO_CODE.put("mexican peso", "MXN");
        NAME_TO_CODE.put("cayman dollar", "KYD");
        NAME_TO_CODE.put("peso", "ARS");
        NAME_TO_CODE.put("argentine peso", "ARS");
        NAME_TO_CODE.put("pound", "GBP");
        NAME_TO_CODE.put("british pound", "GBP");
        NAME_TO_CODE.put("euro", "EUR");
        NAME_TO_CODE.put("yen", "JPY");
        NAME_TO_CODE.put("japanese yen", "JPY");
        NAME_TO_CODE.put("australian dollar", "AUD");
    }

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CurrencyApiService() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    public CurrencyApiService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public BigDecimal fetchExchangeRate(String currencyInput, LocalDate date) throws Exception {
        if (currencyInput == null || currencyInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency input must not be null or empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }

        String normalizedInput = currencyInput.toLowerCase().trim();
        String currencyCode = NAME_TO_CODE.getOrDefault(normalizedInput, normalizedInput.toUpperCase());
        String countryCurrency = CURRENCY_MAPPING.getOrDefault(currencyCode, currencyCode);

        LocalDate sixMonthsPrior = date.minus(6, ChronoUnit.MONTHS);
        String url = buildUrl(countryCurrency, sixMonthsPrior, date);
        String responseBody = sendGetRequest(url);
        return parseExchangeRate(responseBody, countryCurrency, date, sixMonthsPrior);
    }

    private String buildUrl(String countryCurrency, LocalDate startDate, LocalDate endDate) throws UnsupportedEncodingException {
        String encodedCountryCurrency = URLEncoder.encode(countryCurrency, StandardCharsets.UTF_8.toString());
        return BASE_URL +
                "?fields=record_date,country_currency_desc,exchange_rate" +
                "&filter=country_currency_desc:eq:" + encodedCountryCurrency +
                ",record_date:gte:" + startDate +
                ",record_date:lte:" + endDate +
                "&sort=-record_date";
    }

    private String sendGetRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch data from API. Status code: " + response.statusCode());
        }
        return response.body();
    }

    private BigDecimal parseExchangeRate(String jsonResponse, String countryCurrency, LocalDate maxDate, LocalDate minDate) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode dataArray = rootNode.path("data");

        if (dataArray.isEmpty()) {
            throw new IllegalStateException("No exchange rate available for " + countryCurrency + " within 6 months before " + maxDate);
        }

        Iterator<JsonNode> elements = dataArray.elements();
        BigDecimal latestRate = null;
        LocalDate latestDate = null;

        while (elements.hasNext()) {
            JsonNode node = elements.next();
            LocalDate recordDate = LocalDate.parse(node.get("record_date").asText());
            String nodeCountryCurrency = node.get("country_currency_desc").asText();

            if (nodeCountryCurrency.equalsIgnoreCase(countryCurrency) &&
                    !recordDate.isAfter(maxDate) &&
                    !recordDate.isBefore(minDate)) {
                BigDecimal rate = new BigDecimal(node.get("exchange_rate").asText());
                if (latestDate == null || recordDate.isAfter(latestDate)) {
                    latestRate = rate;
                    latestDate = recordDate;
                }
            }
        }

        if (latestRate == null) {
            throw new IllegalStateException("No exchange rate available for " + countryCurrency + " within 6 months before " + maxDate);
        }

        return latestRate;
    }
}
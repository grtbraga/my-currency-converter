package org.currencyconverter;

import org.currencyconverter.model.PurchaseTransaction;
import org.currencyconverter.service.CurrencyApiService;
import org.currencyconverter.service.CurrencyConverterService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverterApp {
    private static final Map<Integer, PurchaseTransaction> transactions = new HashMap<>();
    private static int nextId = 1;

    // Public accessors for testing
    public static Map<Integer, PurchaseTransaction> getTransactions() {
        return transactions;
    }
    public static int getNextId() {
        return nextId;
    }
    public static void setNextId(int id) {
        nextId = id;
    }

    public static void main(String[] args) {
        CurrencyApiService currencyApiService = new CurrencyApiService();
        CurrencyConverterService currencyConverterService = new CurrencyConverterService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add a purchase");
            System.out.println("2. Retrieve a purchase");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            if (option.equals("1")) {
                System.out.print("Enter purchase description: ");
                String description = scanner.nextLine();
                System.out.print("Enter date (yyyy-MM-dd): ");
                LocalDate date = LocalDate.parse(scanner.nextLine());
                System.out.print("Enter amount in USD: ");
                BigDecimal amount = new BigDecimal(scanner.nextLine());

                PurchaseTransaction transaction = new PurchaseTransaction(nextId++, description, date, amount);
                transactions.put(transaction.getId(), transaction);
                System.out.println("Saved with ID: " + transaction.getId());
            }
            else if (option.equals("2")) {
                System.out.print("Enter ID to retrieve: ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter currency (e.g., Euro, EUR, brazilian real, BRL): ");
                String currency = scanner.nextLine();

                try {
                    PurchaseTransaction tx = transactions.get(id);
                    if (tx == null) {
                        System.out.println("No transaction found with ID " + id);
                    } else {
                        BigDecimal rate = currencyApiService.fetchExchangeRate(currency, tx.getTransactionDate());
                        BigDecimal convertedAmount = currencyConverterService.convertToTargetCurrency(tx.getPurchaseAmountUSD(), rate);
                        System.out.println("ID: " + tx.getId());
                        System.out.println("Description: " + tx.getDescription());
                        System.out.println("Date: " + tx.getTransactionDate());
                        System.out.println("USD Amount: " + tx.getPurchaseAmountUSD());
                        System.out.println("Exchange Rate: " + rate);
                        System.out.println("Converted Amount to " + currency + ": " + convertedAmount);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            else if (option.equals("3")) {
                System.out.println("Goodbye!");
                break;
            }
            else {
                System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }
}
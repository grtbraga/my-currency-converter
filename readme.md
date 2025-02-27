# 🌎 Currency Converter App
A simple Java app to store purchases in USD and convert them to other currencies using the Treasury API.

## 🚀 What You Need
- Java 11 (check with `java -version`)
- Maven (check with `mvn -version`)
- Internet connection

## 🔧 How to Run
1. Clone the repo:  
   `git clone <url> && cd <currency_converter_app>`

2. Install dependencies:  
   `mvn dependency:resolve`

3. Build the app:  
   `mvn clean install`

4. Run the app:  
   `java -cp target/currency-converter-app-1.0-SNAPSHOT.jar com.currencyconverter.CurrencyConverterApp`

## 📲 Using It
- Pick an option:
    - Add a purchase — Provide description, date (like 2025-02-26), and USD amount.
    - Look up a purchase — Enter purchase ID and desired currency (e.g., Euro, EUR, BRL, British Pound, GBP, etc.).
    - Exit — Close the app.

## ✅ Running Tests
1. Run all tests:  
   `mvn test`

2. View test reports:  
   Check results in:  
   `target/surefire-reports`

## 📚 Notes
- Transactions stay in memory — no database is used.
- Exchange rates — Fetched from the last 6 months.

## 🛠️ Troubleshooting
- Errors running the app?
    - Verify Java 11 is installed correctly.
    - Check your internet connection.

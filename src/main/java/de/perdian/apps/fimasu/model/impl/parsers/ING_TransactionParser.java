package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionParser;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;

public class ING_TransactionParser implements TransactionParser {

    private static final Logger log = LoggerFactory.getLogger(ING_TransactionParser.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMANY);

    @Override
    public List<Transaction> parseTransactionsFromFile(File documentFile) {
        if (documentFile.getName().indexOf("Direkt_Depot") > -1 && documentFile.getName().indexOf("Abrechnung") > -1 && documentFile.getName().endsWith(".pdf")) {
            try {

                log.debug("Analyzing ING file at: {}", documentFile.getAbsolutePath());
                try (PDDocument pdfDocument = PDDocument.load(documentFile)) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String pdfText = pdfStripper.getText(pdfDocument);
                    StockChangeTransaction transaction = new StockChangeTransaction();
                    try (BufferedReader lineReader = new BufferedReader(new StringReader(pdfText))) {
                        for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                            if (StringUtils.isNotBlank(line)) {
                                this.analyzeLine(line, transaction);
                            }
                        }
                    }
                    return List.of(transaction);
                }

            } catch (Exception e) {
                log.warn("Cannot analyze ING file at: {}", documentFile.getAbsolutePath(), e);
            }
        }
        return Collections.emptyList();
    }

    private void analyzeLine(String line, StockChangeTransaction transaction) throws Exception {
        this.analyzeLineIntoStringWithRegex(line, Pattern.compile("ISIN \\(WKN\\)\\s+([A-Z]{2}[0-9]+)\\s+\\(([A-Z0-9]+)\\)"), transaction.getIsin(), transaction.getWkn());
        this.analyzeLineIntoStringSimple(line, "Wertpapierbezeichnung", transaction.getTitle(), Function.identity());
        this.analyzeLineIntoDoubleSimple(line, "Nominale Stück", "#,##0.00000", transaction.getNumberOfShares());
        this.analyzeLineIntoDoubleWithCurrencySimple(line, "Kurs", "#,##0.00000", transaction.getMarketPrice(), transaction.getMarketCurrency());
        this.analyzeLineIntoStringWithRegex(line, Pattern.compile("Ausführungstag \\/ \\-zeit\\s+([0-9]+\\.[0-9]+\\.[0-9]+).*?"), transaction.getBookingDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        this.analyzeLineIntoStringWithRegex(line, Pattern.compile("Ausführungstag\\s+([0-9]+\\.[0-9]+\\.[0-9]+).*?"), transaction.getBookingDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        this.analyzeLineIntoDoubleWithCurrencySimple(line, "Rabatt", "#,##0.00", transaction.getChargesAmount(), transaction.getChargesCurrency());
        this.analyzeLineIntoDoubleWithCurrencySimple(line, "Provision", "#,##0.00", transaction.getChargesAmount(), transaction.getChargesCurrency());
        this.analyzeLineIntoStringSimple(line, "Valuta", transaction.getValutaDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        this.analyzeLineIntoDoubleWithCurrencySimple(line, "Endbetrag zu Ihren Lasten", "#,##0.00", null, transaction.getBookingCurrency());
        this.analyzeLineIntoDoubleWithCurrencyRegex(line, Pattern.compile("umger\\. zum Devisenkurs \\(([A-Z]{3}) \\= (.*?)\\).*?"), "#,##0.00", transaction.getMarketExchangeRate(), null);
    }

    private void analyzeLineIntoDoubleSimple(String line, String prefix, String numberFormatValue, Property<? super Number> targetProperty) throws Exception {
        if (line.startsWith(prefix)) {
            targetProperty.setValue(this.parseDoubleValue(line.substring(prefix.length()).trim(), "#,##0.00000"));
        }
    }

    private void analyzeLineIntoDoubleWithCurrencySimple(String line, String prefix, String numberFormatValue, DoubleProperty targetValueProperty, StringProperty targetCurrencyProperty) throws Exception {
        if (line.startsWith(prefix)) {
            String remainingLine = line.substring(prefix.length()).trim();
            int nextSpaceIndex = remainingLine.indexOf(" ");
            if (nextSpaceIndex == 3) {
                if (targetCurrencyProperty != null) {
                    targetCurrencyProperty.setValue(remainingLine.substring(0, nextSpaceIndex));
                }
                if (targetValueProperty != null) {
                    targetValueProperty.setValue(this.parseDoubleValue(remainingLine.substring(nextSpaceIndex).trim(), numberFormatValue));
                }
            }
        }
    }

    private void analyzeLineIntoDoubleWithCurrencyRegex(String line, Pattern regexPattern, String numberFormatValue, DoubleProperty targetValueProperty, StringProperty targetCurrencyProperty) throws Exception {
        Matcher regexMatcher = regexPattern.matcher(line);
        if (regexMatcher.matches()) {
            if (targetCurrencyProperty != null) {
                targetCurrencyProperty.setValue(regexMatcher.group(1));
            }
            if (targetValueProperty != null) {
                targetValueProperty.setValue(this.parseDoubleValue(regexMatcher.group(2).trim(), numberFormatValue));
            }
        }
    }

    private <T> void analyzeLineIntoStringSimple(String line, String prefix, Property<T> targetProperty, Function<String, T> stringConverter) {
        if (line.startsWith(prefix)) {
            targetProperty.setValue(stringConverter.apply(line.substring(prefix.length()).trim()));
        }
    }

    private <T> void analyzeLineIntoStringWithRegex(String line, Pattern regexPattern, Property<T> targetProperty, Function<String, T> stringConverter) throws Exception {
        Matcher regexMatcher = regexPattern.matcher(line);
        if (regexMatcher.matches()) {
            targetProperty.setValue(stringConverter.apply(regexMatcher.group(1)));
        }
    }

    @SafeVarargs
    private void analyzeLineIntoStringWithRegex(String line, Pattern regexPattern, Property<String>... targetProperties) throws Exception {
        Matcher regexMatcher = regexPattern.matcher(line);
        if (regexMatcher.matches()) {
            for (int i=0; i < targetProperties.length; i++) {
                targetProperties[i].setValue(regexMatcher.group(i+1));
            }
        }
    }

    private double parseDoubleValue(String inputString, String numberFormatValue) throws Exception {
        NumberFormat numberFormat = new DecimalFormat(numberFormatValue, new DecimalFormatSymbols(Locale.GERMANY));
        Number numberValue = numberFormat.parse(inputString.replaceAll("\\s",  "").trim());
        return numberValue.doubleValue();
    }

}

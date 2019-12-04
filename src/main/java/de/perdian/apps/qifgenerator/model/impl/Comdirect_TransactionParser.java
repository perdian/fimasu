package de.perdian.apps.qifgenerator.model.impl;

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

import de.perdian.apps.qifgenerator.model.Transaction;
import de.perdian.apps.qifgenerator.model.TransactionParser;
import de.perdian.apps.qifgenerator.model.TransactionType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;

public class Comdirect_TransactionParser implements TransactionParser {

    private static final Logger log = LoggerFactory.getLogger(Comdirect_TransactionParser.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMANY);
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00000", new DecimalFormatSymbols(Locale.GERMANY));

    @Override
    public List<Transaction> parseTransactionsFromFile(File documentFile) {
        if (documentFile.getName().startsWith("Wertpapierabrechnung_") && documentFile.getName().endsWith(".pdf")) {
            try {

                log.debug("Analyzing comdirect file at: {}", documentFile.getAbsolutePath());
                try (PDDocument pdfDocument = PDDocument.load(documentFile)) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String pdfText = pdfStripper.getText(pdfDocument);

                    Transaction transaction = new Transaction();
                    transaction.getType().setValue(documentFile.getName().startsWith("Wertpapierabrechnung_Verkauf") ? TransactionType.SELL : TransactionType.BUY);

                    try (BufferedReader lineReader = new BufferedReader(new StringReader(pdfText))) {
                        for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
                            if (StringUtils.isNotBlank(line)) {
                                this.analyzeLine(line.trim(), transaction);
                            }
                        }
                    }
                    return List.of(transaction);
                }

            } catch (Exception e) {
                log.warn("Cannot analyze comdirect file at: {}", documentFile.getAbsolutePath(), e);
            }
        }
        return Collections.emptyList();
    }

    private void analyzeLine(String line, Transaction transaction) throws Exception {
        this.analyzeLineRegex(line, Pattern.compile("Geschäftstag\\s+\\:\\s+(\\d+\\.\\d+\\.\\d+)\\s.*?"), transaction.getBookingDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        this.analyzeLineRegex(line, Pattern.compile("Die Belastung erfolgt mit Valuta (.*?) auf Konto .*"), transaction.getValutaDate(), string -> LocalDate.parse(string, DATE_FORMATTER));
        this.analyzeLineNumberOfSharesAndPrice(line, transaction);
        this.analyzeLineIntoDoubleWithCurrencySimple(line, "Summe Entgelte", transaction.getCharges(), transaction.getChargesCurrency());
        this.analyzeLineWknIsin(line, transaction);
    }

    private <T> void analyzeLineRegex(String line, Pattern regexPattern, Property<T> targetProperty, Function<String, T> stringConverter) throws Exception {
        Matcher regexMatcher = regexPattern.matcher(line);
        if (regexMatcher.matches()) {
            targetProperty.setValue(stringConverter.apply(regexMatcher.group(1)));
        }
    }

    private void analyzeLineNumberOfSharesAndPrice(String line, Transaction transaction) throws Exception {
        if (line.startsWith("St.")) {
            Matcher regexMatcher = Pattern.compile("St\\.\\s+(.*?)\\s+([A-Z]{3})\\s+(.*?)").matcher(line);
            if (regexMatcher.matches()) {
                transaction.getNumberOfShares().setValue(NUMBER_FORMAT.parse(regexMatcher.group(1)).doubleValue());
                transaction.getMarketCurrency().setValue(regexMatcher.group(2));
                transaction.getMarketPrice().setValue(NUMBER_FORMAT.parse(regexMatcher.group(3)).doubleValue());
            }
        }
    }

    private void analyzeLineIntoDoubleWithCurrencySimple(String line, String prefix, DoubleProperty targetValueProperty, StringProperty targetCurrencyProperty) throws Exception {
        if (line.startsWith(prefix)) {
            int nextColonIndex = line.indexOf(":", prefix.length());
            if (nextColonIndex > -1) {
                Matcher remainingLineMatcher = Pattern.compile("([A-Z]{3})\\s+(.*?)").matcher(line.substring(nextColonIndex + 1).trim());
                if (remainingLineMatcher.matches()) {
                    if (targetCurrencyProperty != null) {
                        targetCurrencyProperty.setValue(remainingLineMatcher.group(1));
                    }
                    if (targetValueProperty != null) {
                        targetValueProperty.setValue(NUMBER_FORMAT.parse(remainingLineMatcher.group(2)).doubleValue());
                    }
                }
            }
        }
    }

    private void analyzeLineWknIsin(String line, Transaction transaction) throws Exception {
        Matcher regexMatcher = Pattern.compile("Stk\\..*?\\d+.*?\\s+(.*?)\\,.*?WKN.*?\\:\\s+(.*?)\\s+\\/\\s+(.*?)").matcher(line);
        if (regexMatcher.matches()) {
            transaction.getTitle().setValue(regexMatcher.group(1).trim());
            transaction.getWkn().setValue(regexMatcher.group(2));
            transaction.getIsin().setValue(regexMatcher.group(3));
        }
    }

}

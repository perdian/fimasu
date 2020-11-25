package de.perdian.apps.fimasu.model.impl.parsers;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu.model.TransactionParserHelper;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

public class FrankfurterSparkasse_TransactionParser extends AbstractLineBasedPdfTransactionParser<StockChangeTransaction> {

    private static final NumberFormat AMOUNT_FORMAT = new DecimalFormat("#,###0.00", new DecimalFormatSymbols(Locale.GERMANY));
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?<amount>[0-9,\\.]+)(?<sign>[\\-\\+]?)\\s+(?<currency>[A-Z]{3})");
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[0-9]+\\-.*?\\-[0-9]+\\-[0-9]+\\.pdf");

    @Override
    public boolean canHandleFile(File documentFile) {
        return FILE_NAME_PATTERN.matcher(documentFile.getName()).matches();
    }

    @Override
    protected StockChangeTransaction createTransactionInstance(String pdfText, File pdfFile) {
        return new StockChangeTransaction();
    }

    @Override
    protected void updateTransactionFromLine(StockChangeTransaction transaction, String pdfLine, File pdfFile) throws Exception {
        if (pdfLine.startsWith("Wertpapier Abrechnung Kauf")) {
            transaction.getType().setValue(StockChangeType.BUY);
        } else if (pdfLine.startsWith("Wertpapier Abrechnung Verkauf")) {
            transaction.getType().setValue(StockChangeType.SELL);
        } else {
            TransactionParserHelper.regexSet(pdfLine, "([A-Z]{2}[A-Z0-9]{10})\\s+\\([A-Z0-9]{6}\\)", transaction.getIsin());
            TransactionParserHelper.regexSet(pdfLine, "[A-Z]{2}[A-Z0-9]{10}\\s+\\(([A-Z0-9]{6})\\)", transaction.getWkn());
            this.regexSetAmountWithCurrency(pdfLine, "Ausführungskurs", transaction.getMarketPrice(), transaction.getMarketCurrency());
            TransactionParserHelper.regexSetLocalDate(pdfLine, "Schlusstag/-Zeit ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?", "dd.MM.yyyy", transaction.getBookingDate());
            TransactionParserHelper.regexSetLocalDate(pdfLine, "Den Gegenwert buchen wir mit Valuta ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?", "dd.MM.yyyy", transaction.getValutaDate());
            TransactionParserHelper.regexSetLocalDate(pdfLine, "Schlusstag/-Zeit ([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}).*?", "dd.MM.yyyy", transaction.getBookingDate());
            TransactionParserHelper.regexSetNumber(pdfLine, "Stück (.*?) .+", new DecimalFormat("0.#####"), transaction.getNumberOfShares());
            TransactionParserHelper.regexSet(pdfLine, "Stück .*? (.+)", transaction.getTitle());
            this.regexSetAmountWithCurrency(pdfLine, "Kurswert", transaction.getMarketAmount(), transaction.getMarketCurrency());
            this.regexAddAmountWithCurrency(pdfLine, "Fremde Abwicklungsgebühr für die Umschreibung von Namensaktien", StockChangeType.BUY.equals(transaction.getType()) ? 1d : -1, transaction.getChargesAmount(), transaction.getChargesCurrency());
            this.regexSetAmountWithCurrency(pdfLine, "Ausmachender Betrag", transaction.getTotalAmount(), transaction.getBookingCurrency());
        }
    }

    private void regexSetAmountWithCurrency(String pdfLine, String prefix, DoubleProperty amountProperty, StringProperty currencyProperty) throws Exception {
        if (pdfLine.startsWith(prefix + " ")) {
            Matcher amountMatcher = AMOUNT_PATTERN.matcher(pdfLine.substring(prefix.length() + 1).strip());
            if (amountMatcher.matches()) {
                Number amountValue = AMOUNT_FORMAT.parse(amountMatcher.group("amount"));
                amountProperty.setValue(amountValue.doubleValue());
                currencyProperty.setValue(amountMatcher.group("currency"));
            }
        }
    }

    private void regexAddAmountWithCurrency(String pdfLine, String prefix, double amountSign, DoubleProperty amountProperty, StringProperty currencyProperty) throws Exception {
        if (pdfLine.startsWith(prefix + " ")) {
            Matcher amountMatcher = AMOUNT_PATTERN.matcher(pdfLine.substring(prefix.length() + 1).strip());
            if (amountMatcher.matches()) {
                Number amountValue = AMOUNT_FORMAT.parse(amountMatcher.group("amount"));
                String amountCurrency = amountMatcher.group("currency");
                double sign = "-".equalsIgnoreCase(amountMatcher.group("sign")) ? -1d : 1d;
                double existingAmount = Optional.ofNullable(amountProperty.getValue()).orElse(0d).doubleValue();
                double newAmount = existingAmount + (sign * amountValue.doubleValue() * amountSign);
                if (StringUtils.isNotEmpty(currencyProperty.getValue()) && !Objects.equals(currencyProperty.getValue(), amountCurrency)) {
                    throw new IllegalArgumentException("Currencies do not match! Existing currency: " + currencyProperty.getValue() + ", new currency: " + amountCurrency + " for line: " + pdfLine);
                }
                amountProperty.setValue(newAmount);
                currencyProperty.setValue(amountMatcher.group("currency"));
            }
        }
    }

}

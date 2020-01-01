package de.perdian.apps.fimasu.model.quicken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.model.TransactionType;

public class QifContentGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M.d.yy");
    private static final NumberFormat SHORT_NUMBER_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
    private static final NumberFormat LONG_NUMBER_FORMAT = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));

    public String generate(TransactionGroup transactionGroup) {

        StringBuilder out = new StringBuilder();
        out.append("!Type:Invst\n");

        transactionGroup.getTransactions().stream()
            .filter(transaction -> transaction.getBookingValue().getValue() != null && transaction.getBookingValue().doubleValue() > 0d)
            .filter(transaction -> transaction.getNumberOfShares().getValue() != null && transaction.getNumberOfShares().doubleValue() > 0d)
            .forEach(transaction -> this.appendTransaction(out, transaction, transactionGroup));

        return out.toString();

    }

    private void appendTransaction(StringBuilder out, Transaction transaction, TransactionGroup transactionGroup) {

        Number conversionFactorInput = transaction.getBookingCurrencyExchangeRate().getValue();
        double conversionFactor = conversionFactorInput == null || conversionFactorInput.doubleValue() == 0d ? 1d : conversionFactorInput.doubleValue();

        out.append("D").append(DATE_FORMATTER.format(Optional.ofNullable(transaction.getBookingDate().getValue()).orElseGet(LocalDate::now))).append("\n");
        out.append("V").append(DATE_FORMATTER.format(Optional.ofNullable(transaction.getValutaDate().getValue()).orElseGet(LocalDate::now))).append("\n");
        out.append("N").append(this.resolveType(transaction.getType().getValue())).append("\n");
        out.append("F").append(StringUtils.defaultIfEmpty(transaction.getMarketCurrency().getValue(), "EUR")).append("\n");
        out.append("G").append(LONG_NUMBER_FORMAT.format(conversionFactor)).append("\n");
        out.append("Y").append(transaction.getTitle().getValue()).append("\n");
        out.append("~").append(transaction.getWkn().getValue()).append("\n");
        out.append("@").append(transaction.getIsin().getValue()).append("\n");
        out.append("&").append("2").append("\n");
        out.append("I").append(LONG_NUMBER_FORMAT.format(transaction.getMarketPrice().getValue())).append("\n");
        out.append("Q").append(LONG_NUMBER_FORMAT.format(transaction.getNumberOfShares().getValue())).append("\n");
        out.append("U").append(SHORT_NUMBER_FORMAT.format(transaction.getTotalValue().getValue())).append("\n");
        out.append("O").append(QifComission.compute(transaction)).append("\n");
        out.append("L").append("|[").append(transactionGroup.getAccount().getValue()).append("]").append("\n");
        out.append("$").append(SHORT_NUMBER_FORMAT.format(transaction.getTotalValue().getValue())).append("\n");
        out.append("B").append("0.00|0.00|0.00").append("\n");
        out.append("M").append(this.resolveMemo(transaction)).append("\n");
        out.append("^\n");

    }

    private String resolveType(TransactionType type) {
        switch (type) {
            case BUY:
                return "Kauf";
            case SELL:
                return "Verkauf";
            default:
                return "Kauf";
        }
    }

    private String resolveMemo(Transaction transaction) {
        StringBuilder result = new StringBuilder();
        result.append(SHORT_NUMBER_FORMAT.format(transaction.getNumberOfShares().getValue())).append(" a ");
        result.append(LONG_NUMBER_FORMAT.format(transaction.getMarketPrice().getValue())).append(" ").append(transaction.getMarketCurrency().getValue()).append(" = ");
        result.append(SHORT_NUMBER_FORMAT.format(transaction.getMarketValue().getValue())).append(" ").append(transaction.getMarketCurrency().getValue());
        if (!Objects.equals(transaction.getMarketCurrency().getValue(), transaction.getBookingCurrency().getValue())) {
            result.append(" (= ");
            result.append(SHORT_NUMBER_FORMAT.format(transaction.getBookingValue().getValue())).append(" ").append(transaction.getBookingCurrency().getValue());
            result.append(")");

        }
        return result.toString();
    }

}

package de.perdian.apps.qifgenerator_OLD.fx.quicken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.qifgenerator_OLD.fx.model.Transaction;
import de.perdian.apps.qifgenerator_OLD.fx.model.TransactionGroup;
import de.perdian.apps.qifgenerator_OLD.fx.model.TransactionType;

public class QifContentGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M.d.yy");
    private static final NumberFormat SHORT_NUMBER_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
    private static final NumberFormat LONG_NUMBER_FORMAT = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));

    public String generate(TransactionGroup transactionGroup) {

        StringBuilder out = new StringBuilder();
        out.append("!Type:Invst\n");

        transactionGroup.transactionsProperty().stream()
            .filter(transaction -> transaction.bookingValueProperty().getValue() != null && transaction.bookingValueProperty().doubleValue() > 0d)
            .filter(transaction -> transaction.numberOfSharesProperty().getValue() != null && transaction.numberOfSharesProperty().doubleValue() > 0d)
            .forEach(transaction -> this.appendTransaction(out, transaction, transactionGroup));

        return out.toString();

    }

    private void appendTransaction(StringBuilder out, Transaction transaction, TransactionGroup transactionGroup) {

        Number conversionFactorInput = transaction.bookingCurrencyRateProperty().getValue();
        double conversionFactor = conversionFactorInput == null || conversionFactorInput.doubleValue() == 0d ? 1d : conversionFactorInput.doubleValue();

        out.append("D").append(DATE_FORMATTER.format(Optional.ofNullable(transaction.bookingDateProperty().getValue()).orElseGet(LocalDate::now))).append("\n");
        out.append("V").append(DATE_FORMATTER.format(Optional.ofNullable(transaction.valutaDateProperty().getValue()).orElseGet(LocalDate::now))).append("\n");
        out.append("N").append(this.resolveType(transaction.typeProperty().getValue())).append("\n");
        out.append("F").append(StringUtils.defaultIfEmpty(transaction.marketCurrencyProperty().getValue(), "EUR")).append("\n");
        out.append("G").append(LONG_NUMBER_FORMAT.format(conversionFactor)).append("\n");
        out.append("Y").append(transaction.titleProperty().getValue()).append("\n");
        out.append("~").append(transaction.wknProperty().getValue()).append("\n");
        out.append("@").append(transaction.isinProperty().getValue()).append("\n");
        out.append("&").append("2").append("\n");
        out.append("I").append(LONG_NUMBER_FORMAT.format(transaction.marketPriceProperty().getValue())).append("\n");
        out.append("Q").append(LONG_NUMBER_FORMAT.format(transaction.numberOfSharesProperty().getValue())).append("\n");
        out.append("U").append(SHORT_NUMBER_FORMAT.format(transaction.totalValueProperty().getValue())).append("\n");
        out.append("O").append(QifComission.compute(transaction)).append("\n");
        out.append("L").append("|[").append(transactionGroup.accountProperty().getValue()).append("]").append("\n");
        out.append("$").append(SHORT_NUMBER_FORMAT.format(transaction.totalValueProperty().getValue())).append("\n");
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
        result.append(SHORT_NUMBER_FORMAT.format(transaction.numberOfSharesProperty().getValue())).append(" a ");
        result.append(LONG_NUMBER_FORMAT.format(transaction.marketPriceProperty().getValue())).append(" ").append(transaction.marketCurrencyProperty().getValue()).append(" = ");
        result.append(SHORT_NUMBER_FORMAT.format(transaction.marketValueProperty().getValue())).append(" ").append(transaction.marketCurrencyProperty().getValue());
        if (!Objects.equals(transaction.marketCurrencyProperty().getValue(), transaction.bookingCurrencyProperty().getValue())) {
            result.append(" (= ");
            result.append(SHORT_NUMBER_FORMAT.format(transaction.bookingValueProperty().getValue())).append(" ").append(transaction.bookingCurrencyProperty().getValue());
            result.append(")");

        }
        return result.toString();
    }

}

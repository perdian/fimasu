package de.perdian.apps.fimasu.fx.widgets.transactions.factories;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import de.perdian.apps.fimasu.fx.widgets.transactions.TransactionLine;
import de.perdian.apps.fimasu.fx.widgets.transactions.TransactionLinesFactory;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import de.perdian.commons.fx.properties.converters.DoubleStringConverter;
import javafx.collections.ObservableList;

public class StockChangeTransactionLinesFactory extends TransactionLinesFactory {

    @Override
    public String createTitle() {
        return "Buy/Sell";
    }

    @Override
    protected TransactionLine createHeaderLine(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        TransactionLine headerLine = super.createHeaderLine(transaction, allTransactions, componentBuilder, preferences);
        headerLine.item(componentBuilder.createLabel("Typ"), componentBuilder.createComboBox(((StockChangeTransaction)transaction).getType(), StockChangeType::getTitle, List.of(StockChangeType.BUY, StockChangeType.SELL)).focusTraversable(false).width(70d).get());
        return headerLine;
    }

    @Override
    protected TransactionLine createAmountDetailsLine(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        TransactionLine amountDetailsLine = super.createAmountDetailsLine(transaction, allTransactions, componentBuilder, preferences);
        amountDetailsLine.item(componentBuilder.createLabel("# Shares"), componentBuilder.createTextField(((StockChangeTransaction)transaction).getNumberOfShares(), new DoubleStringConverter(new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get());
        amountDetailsLine.item(componentBuilder.createLabel("Market price / currency "), componentBuilder.createTextField(((StockChangeTransaction)transaction).getMarketPrice(), new DoubleStringConverter(new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.GERMANY)))).width(90d).get(), componentBuilder.createTextField(transaction.getMarketCurrency()).width(50d).get());
        return amountDetailsLine;
    }

}

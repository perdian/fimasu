package de.perdian.apps.fimasu.fx.widgets.transactions.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.perdian.apps.fimasu.fx.widgets.transactions.TransactionDetailsPane;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import de.perdian.commons.fx.properties.converters.DoubleStringConverter;
import de.perdian.commons.fx.properties.converters.LocalDateStringConverter;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class StockChangeTransactionDetailsPane extends TransactionDetailsPane {

    public StockChangeTransactionDetailsPane(StockChangeTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        this.setSpacing(8);
        this.getChildren().add(this.createFirstLinePane(transaction, allTransactions, componentBuilder));
        this.getChildren().add(this.createSecondLinePane(transaction, allTransactions, componentBuilder));
        this.getChildren().add(this.createThirdLinePane(transaction, allTransactions, componentBuilder));
    }

    @Override
    protected String getTitle() {
        return "Buy/Sell";
    }

    private Pane createFirstLinePane(StockChangeTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder) {
        GridPane firstLinePane = new GridPane();
        firstLinePane.setVgap(2);
        firstLinePane.setHgap(4);
        firstLinePane.add(componentBuilder.createLabel("Typ"), 0, 0, 1, 1);
        firstLinePane.add(componentBuilder.createComboBox(transaction.getType(), StockChangeType::getTitle, List.of(Map.entry(StockChangeType.BUY.getTitle(), StockChangeType.BUY), Map.entry(StockChangeType.SELL.getTitle(), StockChangeType.SELL))).focusTraversable(false).width(70d).get(), 0, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("WKN"), 1, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getWkn()).focusTraversable(Bindings.length(transaction.getWkn()).lessThanOrEqualTo(0)).width(75d).get(), 1, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("ISIN"), 2, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getIsin()).focusTraversable(Bindings.length(transaction.getIsin()).lessThanOrEqualTo(0)).width(125d).get(), 2, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Title"), 3, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getTitle()).focusTraversable(Bindings.length(transaction.getTitle()).lessThanOrEqualTo(0)).get(), 3, 1, 1, 1);
        return firstLinePane;
    }

    private Pane createSecondLinePane(StockChangeTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder) {
        GridPane secondLinePane = new GridPane();
        TextField marketExchangeRateField = componentBuilder.createTextField(transaction.getMarketExchangeRate(), new DoubleStringConverter(new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get();
        marketExchangeRateField.disableProperty().bind(Bindings.equal(transaction.getMarketCurrency(), transaction.getBookingCurrency()));
        secondLinePane.add(componentBuilder.createLabel("Booking date"), 0, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getBookingDate(), new LocalDateStringConverter()).width(80d).get(), 0, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Valuta date"), 1, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getValutaDate(), new LocalDateStringConverter()).width(80d).get(), 1, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("# Shares"), 2, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getNumberOfShares(), new DoubleStringConverter(new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 2, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Market price / currency "), 3, 0, 2, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketPrice(), new DoubleStringConverter(new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.GERMANY)))).width(90d).get(), 3, 1, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketCurrency()).width(50d).get(), 4, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Market amount"), 5, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().width(90d).get(), 5, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Booking currency / rate"), 6, 0, 2, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getBookingCurrency()).width(50d).get(), 6, 1, 1, 1);
        secondLinePane.add(marketExchangeRateField, 7, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Converted amount"), 8, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketAmountConverted(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().get(), 8, 1, 1, 1);
        secondLinePane.setVgap(2);
        secondLinePane.setHgap(4);
        return secondLinePane;
    }

    private Pane createThirdLinePane(StockChangeTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder) {
        GridPane thirdLinePane = new GridPane();
        thirdLinePane.add(componentBuilder.createLabel("Charges"), 0, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getChargesAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 0, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createCurrencySelectionComboBox(transaction.getChargesCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get(), 1, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createLabel("Finance tax"), 2, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getFinanceTaxAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 2, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createCurrencySelectionComboBox(transaction.getFinanceTaxCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get(), 3, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createLabel("Solidarity tax"), 4, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getSolidarityTaxAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 4, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createCurrencySelectionComboBox(transaction.getSolidarityTaxCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get(), 5, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createLabel("Booking amount"), 6, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getBookingAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().get(), 6, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getBookingCurrency()).disabled().width(50d).get(), 7, 1, 1, 1);
        thirdLinePane.setVgap(2);
        thirdLinePane.setHgap(4);
        return thirdLinePane;
    }

}

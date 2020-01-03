package de.perdian.apps.fimasu.fx.widgets.transactions.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.fx.widgets.transactions.TransactionPane;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeType;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import de.perdian.commons.fx.properties.converters.DoubleStringConverter;
import de.perdian.commons.fx.properties.converters.LocalDateStringConverter;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class StockChangeTransactionPane extends TransactionPane {

    public StockChangeTransactionPane(StockChangeTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        super(transaction, allTransactions, componentBuilder, preferences);

        this.getChildren().add(this.createFirstLinePane(transaction, allTransactions, componentBuilder));
        this.getChildren().add(this.createSecondLinePane(transaction, allTransactions, componentBuilder));
        this.getChildren().add(this.createThirdLinePane(transaction, allTransactions, componentBuilder));

        this.setSpacing(8);

    }

    private Pane createFirstLinePane(StockChangeTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder) {

        Button deleteButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteButton.setMaxHeight(Double.MAX_VALUE);
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(event -> allTransactions.remove(transaction));
        Button moveUpButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
        moveUpButton.setMaxHeight(Double.MAX_VALUE);
        moveUpButton.disableProperty().bind(Bindings.valueAt(allTransactions, 0).isEqualTo(transaction));
        moveUpButton.setFocusTraversable(false);
        moveUpButton.setOnAction(event -> StockChangeTransactionPane.handleMoveTransaction(transaction, allTransactions, -1));
        Button moveDownButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
        moveDownButton.setMaxHeight(Double.MAX_VALUE);
        moveDownButton.disableProperty().bind(Bindings.valueAt(allTransactions, Bindings.size(allTransactions).subtract(1)).isEqualTo(transaction));
        moveDownButton.setFocusTraversable(false);
        moveDownButton.setOnAction(event -> StockChangeTransactionPane.handleMoveTransaction(transaction, allTransactions, 1));

        HBox buttonBox = new HBox(1);
        buttonBox.getChildren().addAll(deleteButton, moveUpButton, moveDownButton);

        GridPane firstLinePane = new GridPane();
        firstLinePane.setVgap(2);
        firstLinePane.setHgap(4);
        firstLinePane.add(buttonBox, 0, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Typ"), 1, 0, 1, 1);
        firstLinePane.add(componentBuilder.createComboBox(transaction.getType(), StockChangeType::toString, List.of(Map.entry("Buy", StockChangeType.BUY), Map.entry("Sell", StockChangeType.SELL))).focusTraversable(false).width(70d).get(), 1, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("WKN"), 2, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getWkn()).focusTraversable(Bindings.length(transaction.getWkn()).lessThanOrEqualTo(0)).width(75d).get(), 2, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("ISIN"), 3, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getIsin()).focusTraversable(Bindings.length(transaction.getIsin()).lessThanOrEqualTo(0)).width(125d).get(), 3, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Title"), 4, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getTitle()).focusTraversable(Bindings.length(transaction.getTitle()).lessThanOrEqualTo(0)).get(), 4, 1, 1, 1);
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

    private static void handleMoveTransaction(Transaction transaction, ObservableList<Transaction> transactions, int direction) {
        int currentIndex = transactions.indexOf(transaction);
        transactions.remove(transaction);
        if (direction < 0) {
            transactions.add(Math.max(0, currentIndex + direction), transaction);
        } else if (direction > 0) {
            transactions.add(Math.min(transactions.size(), currentIndex + direction), transaction);
        }
    }

}

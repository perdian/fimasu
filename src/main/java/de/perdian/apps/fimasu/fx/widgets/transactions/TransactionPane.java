package de.perdian.apps.fimasu.fx.widgets.transactions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.model.transactions.Transaction;
import de.perdian.apps.fimasu.model.transactions.TransactionType;
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
import javafx.scene.layout.VBox;

public class TransactionPane extends VBox {

    public TransactionPane(Transaction transaction, ObservableList<Transaction> transactions, ComponentBuilder componentBuilder, Preferences preferences) {

        this.getChildren().add(this.createFirstLinePane(componentBuilder, transaction, transactions));
        this.getChildren().add(this.createSecondLinePane(componentBuilder, transaction, transactions));
        this.getChildren().add(this.createThirdLinePane(componentBuilder, transaction, transactions));

        this.setSpacing(8);

    }

    private Pane createFirstLinePane(ComponentBuilder componentBuilder, Transaction transaction, ObservableList<Transaction> transactions) {

        Button deleteButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteButton.setMaxHeight(Double.MAX_VALUE);
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(event -> transactions.remove(transaction));
        Button moveUpButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
        moveUpButton.setMaxHeight(Double.MAX_VALUE);
        moveUpButton.disableProperty().bind(Bindings.valueAt(transactions, 0).isEqualTo(transaction));
        moveUpButton.setFocusTraversable(false);
        moveUpButton.setOnAction(event -> TransactionPane.handleMoveTransaction(transaction, transactions, -1));
        Button moveDownButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
        moveDownButton.setMaxHeight(Double.MAX_VALUE);
        moveDownButton.disableProperty().bind(Bindings.valueAt(transactions, Bindings.size(transactions).subtract(1)).isEqualTo(transaction));
        moveDownButton.setFocusTraversable(false);
        moveDownButton.setOnAction(event -> TransactionPane.handleMoveTransaction(transaction, transactions, 1));

        HBox buttonBox = new HBox(1);
        buttonBox.getChildren().addAll(deleteButton, moveUpButton, moveDownButton);

        GridPane firstLinePane = new GridPane();
        firstLinePane.setVgap(2);
        firstLinePane.setHgap(4);
        firstLinePane.add(buttonBox, 0, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Typ"), 1, 0, 1, 1);
        firstLinePane.add(componentBuilder.createComboBox(transaction.getType(), TransactionType::toString, List.of(Map.entry("Buy", TransactionType.BUY), Map.entry("Sell", TransactionType.SELL))).focusTraversable(false).width(70d).get(), 1, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("WKN"), 2, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getWkn()).focusTraversable(Bindings.length(transaction.getWkn()).lessThanOrEqualTo(0)).width(75d).get(), 2, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("ISIN"), 3, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getIsin()).focusTraversable(Bindings.length(transaction.getIsin()).lessThanOrEqualTo(0)).width(125d).get(), 3, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Title"), 4, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.getTitle()).focusTraversable(Bindings.length(transaction.getTitle()).lessThanOrEqualTo(0)).get(), 4, 1, 1, 1);
        return firstLinePane;

    }

    private Pane createSecondLinePane(ComponentBuilder componentBuilder, Transaction transaction, ObservableList<Transaction> transactions) {
        GridPane secondLinePane = new GridPane();
        TextField bookingCurrencyExchangeRateField = componentBuilder.createTextField(transaction.getBookingCurrencyExchangeRate(), new DoubleStringConverter(new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get();
        bookingCurrencyExchangeRateField.disableProperty().bind(Bindings.equal(transaction.getMarketCurrency(), transaction.getBookingCurrency()));
        secondLinePane.add(componentBuilder.createLabel("Booking date"), 0, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getBookingDate(), new LocalDateStringConverter()).width(80d).get(), 0, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Valuta date"), 1, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getValutaDate(), new LocalDateStringConverter()).width(80d).get(), 1, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("# Shares"), 2, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getNumberOfShares(), new DoubleStringConverter(new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 2, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Market price / currency "), 3, 0, 2, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketPrice(), new DoubleStringConverter(new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.GERMANY)))).width(90d).get(), 3, 1, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketCurrency()).width(50d).get(), 4, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Market value"), 5, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getMarketValue(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().width(90d).get(), 5, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Booking currency / rate"), 6, 0, 2, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getBookingCurrency()).width(50d).get(), 6, 1, 1, 1);
        secondLinePane.add(bookingCurrencyExchangeRateField, 7, 1, 1, 1);
        secondLinePane.add(componentBuilder.createLabel("Booking value"), 8, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.getBookingValue(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().get(), 8, 1, 1, 1);
        secondLinePane.setVgap(2);
        secondLinePane.setHgap(4);
        return secondLinePane;
    }

    private Pane createThirdLinePane(ComponentBuilder componentBuilder, Transaction transaction, ObservableList<Transaction> transactions) {
        GridPane thirdLinePane = new GridPane();
        thirdLinePane.add(componentBuilder.createLabel("Charges"), 0, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getCharges(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 0, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createCurrencySelectionComboBox(transaction.getChargesCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get(), 1, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createLabel("Finance tax"), 2, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getFinanceTax(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 2, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createCurrencySelectionComboBox(transaction.getFinanceTaxCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get(), 3, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createLabel("Solidarity tax"), 4, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getSolidarityTax(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), 4, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createCurrencySelectionComboBox(transaction.getSolidarityTaxCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get(), 5, 1, 1, 1);
        thirdLinePane.add(componentBuilder.createLabel("Total value"), 6, 0, 2, 1);
        thirdLinePane.add(componentBuilder.createTextField(transaction.getTotalValue(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().get(), 6, 1, 1, 1);
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

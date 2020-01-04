package de.perdian.apps.fimasu.fx.widgets.transactions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.fx.widgets.transactions.actions.MoveTransactionActionEventHandler;
import de.perdian.apps.fimasu.fx.widgets.transactions.factories.PayoutTransactionLinesFactory;
import de.perdian.apps.fimasu.fx.widgets.transactions.factories.StockChangeTransactionLinesFactory;
import de.perdian.apps.fimasu.fx.widgets.transactions.support.LoadStockInfoChangeListener;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.PayoutTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.apps.fimasu.support.stockinfo.StockInfoProvider;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import de.perdian.commons.fx.properties.converters.DoubleStringConverter;
import de.perdian.commons.fx.properties.converters.LocalDateStringConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public abstract class TransactionLinesFactory {

    public static TransactionLinesFactory forTransaction(Transaction transaction) {
        if (transaction instanceof StockChangeTransaction) {
            return new StockChangeTransactionLinesFactory();
        } else if (transaction instanceof PayoutTransaction) {
            return new PayoutTransactionLinesFactory();
        } else {
            throw new IllegalArgumentException("Don't know how to build a TransactionDetailsPane for class: " + transaction.getClass().getName());
        }
    }

    protected abstract String createTitle();

    protected List<TransactionLine> createLines(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        List<TransactionLine> transactionLines = new ArrayList<>();
        transactionLines.add(this.createHeaderLine(transaction, allTransactions, componentBuilder, preferences));
        transactionLines.add(this.createAmountLine(transaction, allTransactions, componentBuilder, preferences));
        transactionLines.add(this.createChargesAndTotalsLine(transaction, allTransactions, componentBuilder, preferences));
        return transactionLines;
    }

    protected TransactionLine createHeaderLine(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {

        ToggleButton persistButton = new ToggleButton(null, new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        persistButton.setMaxHeight(Double.MAX_VALUE);
        persistButton.setFocusTraversable(false);
        persistButton.setTooltip(new Tooltip("Save transaction when existing the application"));
        persistButton.selectedProperty().bindBidirectional(transaction.getPersistent());
        Button deleteButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteButton.setMaxHeight(Double.MAX_VALUE);
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(event -> allTransactions.remove(transaction));
        Button moveUpButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
        moveUpButton.setMaxHeight(Double.MAX_VALUE);
        moveUpButton.disableProperty().bind(Bindings.valueAt(allTransactions, 0).isEqualTo(transaction));
        moveUpButton.setFocusTraversable(false);
        moveUpButton.setOnAction(new MoveTransactionActionEventHandler(transaction, allTransactions, -1));
        Button moveDownButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
        moveDownButton.setMaxHeight(Double.MAX_VALUE);
        moveDownButton.disableProperty().bind(Bindings.valueAt(allTransactions, Bindings.size(allTransactions).subtract(1)).isEqualTo(transaction));
        moveDownButton.setFocusTraversable(false);
        moveDownButton.setOnAction(new MoveTransactionActionEventHandler(transaction, allTransactions, 1));
        HBox buttonsBox = new HBox(1, persistButton, deleteButton, moveUpButton, moveDownButton);

        TextField wknField = componentBuilder.createTextField(transaction.getWkn()).focusTraversable(Bindings.length(transaction.getWkn()).lessThanOrEqualTo(0)).width(75d).get();
        TextField isinField = componentBuilder.createTextField(transaction.getIsin()).focusTraversable(Bindings.length(transaction.getIsin()).lessThanOrEqualTo(0)).width(125d).get();
        TextField titleField = componentBuilder.createTextField(transaction.getTitle()).focusTraversable(Bindings.length(transaction.getTitle()).lessThanOrEqualTo(0)).get();
        BooleanProperty loadStockInfoBusy = new SimpleBooleanProperty(false);
        wknField.textProperty().addListener(new LoadStockInfoChangeListener(transaction, wkn -> StockInfoProvider.resolveStockInfoProvider().findStockInfoByWkn(wkn), 6, List.of(isinField, titleField), loadStockInfoBusy));
        isinField.textProperty().addListener(new LoadStockInfoChangeListener(transaction, isin -> StockInfoProvider.resolveStockInfoProvider().findStockInfoByIsin(isin), 12, List.of(wknField, titleField), loadStockInfoBusy));

        TransactionLine transactionLine = new TransactionLine();
        transactionLine.item(componentBuilder.createLabel(this.createTitle()), buttonsBox);
        transactionLine.item(componentBuilder.createLabel("WKN"), wknField);
        transactionLine.item(componentBuilder.createLabel("ISIN"), isinField);
        transactionLine.item(componentBuilder.createLabel("Title"), titleField);
        return transactionLine;

    }

    protected TransactionLine createAmountLine(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {

        TextField marketExchangeRateField = componentBuilder.createTextField(transaction.getMarketExchangeRate(), new DoubleStringConverter(new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get();
        marketExchangeRateField.disableProperty().bind(Bindings.equal(transaction.getMarketCurrency(), transaction.getBookingCurrency()));

        TransactionLine transactionLine = new TransactionLine();
        transactionLine.item(componentBuilder.createLabel("Booking date"), componentBuilder.createTextField(transaction.getBookingDate(), new LocalDateStringConverter()).width(80d).get());
        transactionLine.item(componentBuilder.createLabel("Valuta date"), componentBuilder.createTextField(transaction.getValutaDate(), new LocalDateStringConverter()).width(80d).get());
        transactionLine.getItems().addAll(this.createAmountDetailsLine(transaction, allTransactions, componentBuilder, preferences).getItems());
        transactionLine.item(componentBuilder.createLabel("Market amount"), componentBuilder.createTextField(transaction.getMarketAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled(transaction.computeMarketAmountEditable().not()).width(90d).get());
        transactionLine.item(componentBuilder.createLabel("Booking currency / rate"), componentBuilder.createTextField(transaction.getBookingCurrency()).width(50d).get(), marketExchangeRateField);
        transactionLine.item(componentBuilder.createLabel("Booking amount"), componentBuilder.createTextField(transaction.getBookingAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().get());
        return transactionLine;

    }

    protected TransactionLine createAmountDetailsLine(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        return new TransactionLine();
    }

    protected TransactionLine createChargesAndTotalsLine(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        BooleanExpression taxesDisabled = transaction.computeTaxesEditable().not();
        TransactionLine transactionLine = new TransactionLine();
        transactionLine.item(componentBuilder.createLabel("Charges"), componentBuilder.createTextField(transaction.getChargesAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).width(80d).get(), componentBuilder.createCurrencySelectionComboBox(transaction.getChargesCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency())).focusTraversable(false).width(80d).get());
        transactionLine.item(componentBuilder.createLabel("Finance tax"),  componentBuilder.createTextField(transaction.getFinanceTaxAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled(taxesDisabled).width(80d).get(), componentBuilder.createCurrencySelectionComboBox(transaction.getFinanceTaxCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency()), taxesDisabled).focusTraversable(false).width(80d).get());
        transactionLine.item(componentBuilder.createLabel("Solidarity tax"), componentBuilder.createTextField(transaction.getSolidarityTaxAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled(taxesDisabled).width(80d).get(), componentBuilder.createCurrencySelectionComboBox(transaction.getSolidarityTaxCurrency(), List.of(transaction.getMarketCurrency(), transaction.getBookingCurrency()), taxesDisabled).focusTraversable(false).width(80d).get());
        transactionLine.item(componentBuilder.createLabel("Total amount"), componentBuilder.createTextField(transaction.getTotalAmount(), new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)))).disabled().get(), componentBuilder.createTextField(transaction.getBookingCurrency()).disabled().width(50d).get());
        return transactionLine;
    }

}

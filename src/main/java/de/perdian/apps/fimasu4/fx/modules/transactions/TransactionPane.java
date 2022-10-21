package de.perdian.apps.fimasu4.fx.modules.transactions;

import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;

import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import de.perdian.apps.fimasu4.fx.support.converters.EnumStringConverter;
import de.perdian.apps.fimasu4.fx.support.converters.LocalDateStringConverter;
import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class TransactionPane extends VBox {

    private Transaction transaction = null;

    TransactionPane(Transaction transaction, ObservableList<Transaction> allTransactions) {

        ComponentFactory componentFactory = new ComponentFactory();

        Pane topPane = this.createTopPane(transaction, allTransactions, componentFactory);
        Pane middlePane = this.createMiddlePane(transaction, allTransactions, componentFactory);
        Pane bottomPane = this.createBottomPane(transaction, allTransactions, componentFactory);
        this.getChildren().addAll(topPane, middlePane, bottomPane);

        this.setSpacing(5);
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setTransaction(transaction);

    }

    private Pane createTopPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentFactory componentFactory) {

        Pane actionsPane = this.createActionsPane(transaction, allTransactions, componentFactory);
        Label actionsLabel = componentFactory.createLabel(transaction.getType());
        actionsLabel.setLabelFor(actionsPane);

        TextField wknField = componentFactory.createTextField(transaction.getStockIdentifier().getWkn());
        wknField.setPrefWidth(70);
        Label wknLabel = componentFactory.createLabel("WKN");
        wknLabel.setLabelFor(wknField);

        TextField isinField = componentFactory.createTextField(transaction.getStockIdentifier().getIsin());
        isinField.setPrefWidth(130);
        Label isinLabel = componentFactory.createLabel("ISIN");
        isinLabel.setLabelFor(isinField);

        TextField titleField = componentFactory.createTextField(transaction.getStockIdentifier().getTitle());
        GridPane.setHgrow(titleField, Priority.ALWAYS);
        Label titleLabel = componentFactory.createLabel("Title");
        titleLabel.setLabelFor(titleField);

        ComboBox<TransactionType> typeBox = componentFactory.createComboBox(transaction.getType(), new EnumStringConverter<>(TransactionType.class), FXCollections.observableArrayList(TransactionType.values()));
        Label typeLabel = componentFactory.createLabel("Type");
        typeLabel.setLabelFor(typeBox);

        GridPane topPane = new GridPane();
        topPane.setHgap(5);
        topPane.setVgap(2);
        topPane.add(actionsLabel, 0, 0, 1, 1);
        topPane.add(actionsPane, 0, 1, 1, 1);
        topPane.add(wknLabel, 1, 0, 1, 1);
        topPane.add(wknField, 1, 1, 1, 1);
        topPane.add(isinLabel, 2, 0, 1, 1);
        topPane.add(isinField, 2, 1, 1, 1);
        topPane.add(titleLabel, 3, 0, 1, 1);
        topPane.add(titleField, 3, 1, 1, 1);
        topPane.add(typeLabel, 4, 0, 1, 1);
        topPane.add(typeBox, 4, 1, 1, 1);
        return topPane;

    }

    private Pane createActionsPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentFactory componentFactory) {

        ToggleButton persistentButton = componentFactory.createToggleButton(transaction.getPersistent(), MaterialDesignC.CONTENT_SAVE);
        Button deleteButton = componentFactory.createButton(MaterialDesignT.TRASH_CAN_OUTLINE, event -> this.handleTransactionDelete(transaction, allTransactions));
        Button moveUpButton = componentFactory.createButton(MaterialDesignA.ARROW_UP, event -> this.handleTransactionMove(transaction, allTransactions, -1));
        moveUpButton.disableProperty().bind(Bindings.valueAt(allTransactions, 0).isEqualTo(transaction));
        Button moveDownButton = componentFactory.createButton(MaterialDesignA.ARROW_DOWN, event -> this.handleTransactionMove(transaction, allTransactions, 1));
        moveDownButton.disableProperty().bind(Bindings.valueAt(allTransactions, Bindings.size(allTransactions).subtract(1)).isEqualTo(transaction));

        HBox actionsPane = new HBox(1);
        actionsPane.getChildren().addAll(persistentButton, deleteButton, moveUpButton, moveDownButton);
        return actionsPane;

    }

    private Pane createMiddlePane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentFactory componentFactory) {

        TextField bookingDateField = componentFactory.createTextField(transaction.getBookingDate(), new LocalDateStringConverter());
        bookingDateField.setPrefWidth(85);
        Label bookingDateLabel = componentFactory.createLabel("Booking date");
        bookingDateLabel.setLabelFor(bookingDateField);
        TextField valutaDateField = componentFactory.createTextField(transaction.getValutaDate(), new LocalDateStringConverter());
        valutaDateField.setPrefWidth(85);
        Label valutaDateLabel = componentFactory.createLabel("Valuta date");
        valutaDateLabel.setLabelFor(valutaDateField);

        BooleanBinding buyOrSellBinding = transaction.getType().isEqualTo(TransactionType.BUY).or(transaction.getType().isEqualTo(TransactionType.SELL));
        BooleanBinding payoutBinding = transaction.getType().isEqualTo(TransactionType.PAYOUT);
        StringProperty bookingInputCurrency = new SimpleStringProperty("EUR");
        if (buyOrSellBinding.getValue()) {
            bookingInputCurrency.setValue(transaction.getStockValue().getCurrency().getValue());
        } else if (payoutBinding.get()) {
            bookingInputCurrency.setValue(transaction.getPayoutValue().getCurrency().getValue());
        }
        ChangeListener<Object> refreshBookingInputCurrencyChangeListener = (o, oldValue, newValue) -> {
            if (TransactionType.BUY.equals(transaction.getType().getValue()) || TransactionType.SELL.equals(transaction.getType().getValue())) {
                bookingInputCurrency.setValue(transaction.getStockValue().getCurrency().getValue());
            } else if (TransactionType.PAYOUT.equals(transaction.getType().getValue())) {
                bookingInputCurrency.setValue(transaction.getPayoutValue().getCurrency().getValue());
            }
        };
        transaction.getType().addListener(refreshBookingInputCurrencyChangeListener);
        transaction.getPayoutValue().getCurrency().addListener(refreshBookingInputCurrencyChangeListener);
        transaction.getStockValue().getCurrency().addListener(refreshBookingInputCurrencyChangeListener);

        TextField stockCountField = componentFactory.createDecimalField(transaction.getStockCount(), 5);
        stockCountField.setPrefWidth(100);
        stockCountField.disableProperty().bind(buyOrSellBinding.not());
        Label stockCountLabel = componentFactory.createLabel("# of stocks");
        stockCountLabel.setLabelFor(stockCountField);

        TextField stockPriceAmountField = componentFactory.createDecimalField(transaction.getStockPrice().getAmount(), 5);
        stockPriceAmountField.setPrefWidth(100);
        stockPriceAmountField.disableProperty().bind(buyOrSellBinding.not());
        TextField stockPriceCurrencyField = componentFactory.createCurrencyField(transaction.getStockPrice().getCurrency());
        stockPriceCurrencyField.disableProperty().bind(buyOrSellBinding.not());
        Label stockPriceLabel = componentFactory.createLabel("Stock price / currency");
        stockPriceLabel.setLabelFor(stockPriceAmountField);

        TextField stockValueAmountField = componentFactory.createDecimalField(transaction.getStockValue().getAmount(), 5);
        stockValueAmountField.setPrefWidth(100);
        stockValueAmountField.setDisable(true);
        TextField stockValueCurrencyField = componentFactory.createCurrencyField(transaction.getStockValue().getCurrency());
        stockValueCurrencyField.setDisable(true);
        Label stockValueLabel = componentFactory.createLabel("Stock value / currency");
        stockValueLabel.setLabelFor(stockValueAmountField);

        TextField payoutAmountField = componentFactory.createDecimalField(transaction.getPayoutValue().getAmount(), 5);
        payoutAmountField.setPrefWidth(100);
        payoutAmountField.disableProperty().bind(payoutBinding.not());
        TextField payoutCurrencyField = componentFactory.createCurrencyField(transaction.getPayoutValue().getCurrency());
        payoutCurrencyField.disableProperty().bind(payoutBinding.not());
        Label payoutLabel = componentFactory.createLabel("Payout value / currency");
        payoutLabel.setLabelFor(payoutAmountField);

        TextField bookingValueCurrencyField = componentFactory.createCurrencyField(transaction.getBookingValue().getCurrency());
        TextField bookingValueConversionRateField = componentFactory.createDecimalField(transaction.getBookingConversionRate(), 5);
        bookingValueConversionRateField.setPrefWidth(100);
        Label bookingValueCurrencyLabel = componentFactory.createLabel("Booking currency / rate");
        bookingValueCurrencyLabel.setLabelFor(bookingValueCurrencyField);
        TextField bookingValueAmountField = componentFactory.createDecimalField(transaction.getBookingValue().getAmount(), 5);
        bookingValueAmountField.setPrefWidth(100);
        bookingValueAmountField.setDisable(true);
        GridPane.setHgrow(bookingValueAmountField, Priority.ALWAYS);
        Label bookingValueAmountLabel = componentFactory.createLabel("Booking value");
        bookingValueAmountLabel.setLabelFor(bookingValueAmountField);
        bookingValueConversionRateField.disableProperty().bind(Bindings.equal(bookingInputCurrency, transaction.getBookingValue().getCurrency()));
        TextField bookingValueCurrencyField2 = componentFactory.createCurrencyField(transaction.getBookingValue().getCurrency());
        bookingValueCurrencyField2.setDisable(true);

        GridPane middlePane = new GridPane();
        middlePane.add(bookingDateLabel, 0, 0, 1, 1);
        middlePane.add(bookingDateField, 0, 1, 1, 1);
        middlePane.add(valutaDateLabel, 1, 0, 1, 1);
        middlePane.add(valutaDateField, 1, 1, 1, 1);
        middlePane.add(stockCountLabel, 2, 0, 1, 1);
        middlePane.add(stockCountField, 2, 1, 1, 1);
        middlePane.add(stockPriceLabel, 3, 0, 2, 1);
        middlePane.add(stockPriceAmountField, 3, 1, 1, 1);
        middlePane.add(stockPriceCurrencyField, 4, 1, 1, 1);
        middlePane.add(stockValueLabel, 5, 0, 2, 1);
        middlePane.add(stockValueAmountField, 5, 1, 1, 1);
        middlePane.add(stockValueCurrencyField, 6, 1, 1, 1);
        middlePane.add(payoutLabel, 7, 0, 2, 1);
        middlePane.add(payoutAmountField, 7, 1, 1, 1);
        middlePane.add(payoutCurrencyField, 8, 1, 1, 1);
        middlePane.add(bookingValueCurrencyLabel, 9, 0, 2, 1);
        middlePane.add(bookingValueCurrencyField, 9, 1, 1, 1);
        middlePane.add(bookingValueConversionRateField, 10, 1, 1, 1);
        middlePane.add(bookingValueAmountLabel, 11, 0, 1, 1);
        middlePane.add(bookingValueAmountField, 11, 1, 1, 1);
        middlePane.add(bookingValueCurrencyField2, 12, 1, 1, 1);
        middlePane.setHgap(5);
        middlePane.setVgap(2);
        return middlePane;

    }

    private Pane createBottomPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentFactory componentFactory) {

        TextField additionalChargesValueField = componentFactory.createDecimalField(transaction.getAdditionalCharges().getAmount(), 2);
        additionalChargesValueField.setPrefWidth(85);
        ComboBox<String> additionalChargesCurrencyBox = componentFactory.createCurrencyComboBox(transaction.getAdditionalCharges().getCurrency(), transaction.getAvailableCurrencies());
        Label additionalChargesLabel = componentFactory.createLabel("Charges");
        additionalChargesLabel.setLabelFor(additionalChargesValueField);

        TextField additionalFinanceTaxValueField = componentFactory.createDecimalField(transaction.getAdditionalFinanceTax().getAmount(), 2);
        additionalFinanceTaxValueField.setPrefWidth(85);
        ComboBox<String> additionalFinanceTaxCurrencyBox = componentFactory.createCurrencyComboBox(transaction.getAdditionalFinanceTax().getCurrency(), transaction.getAvailableCurrencies());
        Label additionalFinanceTaxLabel = componentFactory.createLabel("Finance tax");
        additionalFinanceTaxLabel.setLabelFor(additionalFinanceTaxValueField);

        TextField additionalSolidarityTaxValueField = componentFactory.createDecimalField(transaction.getAdditionalSolidarityTax().getAmount(), 2);
        additionalSolidarityTaxValueField.setPrefWidth(85);
        ComboBox<String> additionalSolidarityTaxCurrencyBox = componentFactory.createCurrencyComboBox(transaction.getAdditionalSolidarityTax().getCurrency(), transaction.getAvailableCurrencies());
        Label additionalSolidarityTaxLabel = componentFactory.createLabel("Solidarity tax");
        additionalSolidarityTaxLabel.setLabelFor(additionalSolidarityTaxValueField);

        TextField totalValueField = componentFactory.createDecimalField(transaction.getTotalValue().getAmount(), 2);
        totalValueField.setPrefWidth(85);
        totalValueField.setDisable(true);
        GridPane.setHgrow(totalValueField, Priority.ALWAYS);
        TextField totalCurrencyField = componentFactory.createCurrencyField(transaction.getTotalValue().getCurrency());
        totalCurrencyField.setDisable(true);
        Label totalLabel = componentFactory.createLabel("Total");
        totalLabel.setLabelFor(totalValueField);

        GridPane bottomPane = new GridPane();
        bottomPane.add(additionalChargesLabel, 0, 0, 2, 1);
        bottomPane.add(additionalChargesValueField, 0, 1, 1, 1);
        bottomPane.add(additionalChargesCurrencyBox, 1, 1, 1, 1);
        bottomPane.add(additionalFinanceTaxLabel, 2, 0, 2, 1);
        bottomPane.add(additionalFinanceTaxValueField, 2, 1, 1, 1);
        bottomPane.add(additionalFinanceTaxCurrencyBox, 3, 1, 1, 1);
        bottomPane.add(additionalSolidarityTaxLabel, 4, 0, 2, 1);
        bottomPane.add(additionalSolidarityTaxValueField, 4, 1, 1, 1);
        bottomPane.add(additionalSolidarityTaxCurrencyBox, 5, 1, 1, 1);
        bottomPane.add(totalLabel, 6, 0, 2, 1);
        bottomPane.add(totalValueField, 6, 1, 1, 1);
        bottomPane.add(totalCurrencyField, 7, 1, 1, 1);
        bottomPane.setHgap(5);
        bottomPane.setVgap(2);
        return bottomPane;

    }

    private void handleTransactionMove(Transaction transaction, ObservableList<Transaction> allTransactions, int direction) {
        int oldIndex = allTransactions.indexOf(transaction);
        int newIndex = oldIndex + direction;
        allTransactions.remove(oldIndex);
        allTransactions.add(newIndex, transaction);
    }

    private void handleTransactionDelete(Transaction transaction, ObservableList<Transaction> allTransactions) {
        allTransactions.remove(transaction);
    }

    Transaction getTransaction() {
        return this.transaction;
    }
    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

}

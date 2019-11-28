package de.perdian.apps.qifgenerator_OLD.fx.modules.transactions;

import de.perdian.apps.qifgenerator.fx.support.converters.LocalDateStringConverter;
import de.perdian.apps.qifgenerator_OLD.fx.model.Transaction;
import de.perdian.apps.qifgenerator_OLD.fx.model.TransactionType;
import de.perdian.apps.qifgenerator_OLD.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator_OLD.fx.support.components.converters.DoubleStringConverter;
import de.perdian.apps.qifgenerator_OLD.fx.support.components.impl.MultipleCurrencyValueField;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class TransactionPane extends VBox {

    private Transaction transaction = null;

    TransactionPane(Transaction transaction, ObservableList<Transaction> transactions, ComponentBuilder parentComponentBuilder) {

        ComponentBuilder componentBuilder = new ComponentBuilder(parentComponentBuilder);
        componentBuilder.addOnKeyPressedEventHandler(this::handleKeyPressedEvent);

        Button removeButton = new Button();
        removeButton.setFocusTraversable(false);
        removeButton.setGraphic(new ImageView(new Image(TransactionPane.class.getClassLoader().getResourceAsStream("icons/16/delete.png"))));
        removeButton.setOnAction(event -> transactions.remove(transaction));
        Button upButton = new Button();
        upButton.setFocusTraversable(false);
        upButton.setGraphic(new ImageView(new Image(TransactionPane.class.getClassLoader().getResourceAsStream("icons/16/go-up.png"))));
        upButton.setOnAction(event -> this.handleMoveTransaction(transaction, transactions, -1));
        Button downButton = new Button();
        downButton.setFocusTraversable(false);
        downButton.setGraphic(new ImageView(new Image(TransactionPane.class.getClassLoader().getResourceAsStream("icons/16/go-down.png"))));
        downButton.setOnAction(event -> this.handleMoveTransaction(transaction, transactions, 1));
        HBox buttonBox = new HBox(removeButton, upButton, downButton);

        ComboBox<TransactionType> typeBox = componentBuilder.createComboBox(transaction.typeProperty(), TransactionType.values());
        TextField wknField = componentBuilder.createTextField(transaction.wknProperty());
        wknField.setPrefWidth(80);
        TextField isinField = componentBuilder.createTextField(transaction.isinProperty());
        isinField.setPrefWidth(120);
        TextField titleField = componentBuilder.createTextField(transaction.titleProperty());
        GridPane.setHgrow(titleField, Priority.ALWAYS);

        GridPane topPane = new GridPane();
        topPane.setHgap(4);
        topPane.setVgap(2);
        topPane.add(buttonBox, 0, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("Type"), 1, 0, 1, 1);
        topPane.add(typeBox, 1, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("WKN"), 2, 0, 1, 1);
        topPane.add(wknField, 2, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("ISIN"), 3, 0, 1, 1);
        topPane.add(isinField, 3, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("Title"), 4, 0, 1, 1);
        topPane.add(titleField, 4, 1, 1, 1);

        TextField bookingDateField = componentBuilder.createTextField(transaction.bookingDateProperty(), new LocalDateStringConverter());
        bookingDateField.setPrefWidth(85);
        TextField valutaDateField = componentBuilder.createTextField(transaction.valutaDateProperty(), new LocalDateStringConverter());
        valutaDateField.setPrefWidth(85);
        TextField numberOfSharesField = componentBuilder.createTextField(transaction.numberOfSharesProperty(), new DoubleStringConverter("0.00000"));
        numberOfSharesField.setPrefWidth(85);
        TextField marketPriceField = componentBuilder.createTextField(transaction.marketPriceProperty(), new DoubleStringConverter("0.00000"));
        marketPriceField.setPrefWidth(85);
        TextField marketCurrencyField = componentBuilder.createTextField(transaction.marketCurrencyProperty());
        marketCurrencyField.setPrefWidth(60);
        TextField marketValueField = componentBuilder.createTextField(transaction.marketValueProperty(), new DoubleStringConverter("0.00"));
        marketValueField.setDisable(true);
        marketValueField.setPrefWidth(85);
        TextField bookingCurrencyField = componentBuilder.createTextField(transaction.bookingCurrencyProperty());
        bookingCurrencyField.setPrefWidth(60);
        TextField bookingCurrencyRateField = componentBuilder.createTextField(transaction.bookingCurrencyRateProperty(), new DoubleStringConverter("0.00000"));
        bookingCurrencyRateField.disableProperty().bind(transaction.bookingCurrencyDifferentProperty().not());
        bookingCurrencyRateField.setPrefWidth(85);
        TextField bookingValueField = componentBuilder.createTextField(transaction.bookingValueProperty(), new DoubleStringConverter("0.00"));
        bookingValueField.setPrefWidth(85);
        bookingValueField.setDisable(true);
        GridPane.setHgrow(bookingValueField, Priority.ALWAYS);
        GridPane middlePane = new GridPane();
        middlePane.setHgap(4);
        middlePane.setVgap(2);
        middlePane.add(componentBuilder.createLabel("Booking date"), 0, 0, 1, 1);
        middlePane.add(bookingDateField, 0, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("Valuta date"), 1, 0, 1, 1);
        middlePane.add(valutaDateField, 1, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("# Shares"), 2, 0, 1, 1);
        middlePane.add(numberOfSharesField, 2, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("Market price"), 3, 0, 1, 1);
        middlePane.add(marketPriceField, 3, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("Currency"), 4, 0, 1, 1);
        middlePane.add(marketCurrencyField, 4, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("Market value"), 5, 0, 1, 1);
        middlePane.add(marketValueField, 5, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("Booking currency / rate"), 6, 0, 2, 1);
        middlePane.add(bookingCurrencyField, 6, 1, 1, 1);
        middlePane.add(bookingCurrencyRateField, 7, 1, 1, 1);
        middlePane.add(componentBuilder.createLabel("Booking value"), 8, 0, 1, 1);
        middlePane.add(bookingValueField, 8, 1, 1, 1);

        MultipleCurrencyValueField chargesField = componentBuilder.createMultiCurrencyInputField(transaction.chargesProperty(), transaction.bookingCurrencyProperty(), transaction.marketCurrencyProperty(), transaction.marketCurrencyProperty(), transaction.bookingCurrencyRateProperty());
        MultipleCurrencyValueField financeTaxField = componentBuilder.createMultiCurrencyInputField(transaction.financeTaxProperty(), transaction.bookingCurrencyProperty(), transaction.marketCurrencyProperty(), transaction.bookingCurrencyProperty(), transaction.bookingCurrencyRateProperty());
        MultipleCurrencyValueField solidarityTaxField = componentBuilder.createMultiCurrencyInputField(transaction.solidarityTaxProperty(), transaction.bookingCurrencyProperty(), transaction.marketCurrencyProperty(), transaction.bookingCurrencyProperty(), transaction.bookingCurrencyRateProperty());
        TextField totalValueField = componentBuilder.createTextField(transaction.totalValueProperty(), new DoubleStringConverter("0.00"));
        totalValueField.setPrefWidth(85);
        totalValueField.setDisable(true);
        TextField totalValueCurrencyField = componentBuilder.createTextField(transaction.bookingCurrencyProperty());
        totalValueCurrencyField.setPrefWidth(60);
        totalValueCurrencyField.setDisable(true);
        GridPane bottomPane = new GridPane();
        bottomPane.setHgap(4);
        bottomPane.setVgap(2);
        bottomPane.add(componentBuilder.createLabel("Charges"), 0, 0, 1, 1);
        bottomPane.add(chargesField, 0, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Fin. tax"), 1, 0, 1, 1);
        bottomPane.add(financeTaxField, 1, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Sol. tax"), 2, 0, 1, 1);
        bottomPane.add(solidarityTaxField, 2, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Total value"), 3, 0, 2, 1);
        bottomPane.add(totalValueField, 3, 1, 1, 1);
        bottomPane.add(totalValueCurrencyField, 4, 1, 1, 1);

        this.getChildren().add(topPane);
        this.getChildren().add(middlePane);
        this.getChildren().add(bottomPane);
        this.setSpacing(8);
        this.setTransaction(transaction);

    }

    private void handleKeyPressedEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Event.fireEvent(event.getTarget(), new KeyEvent(event.getSource(), event.getTarget(), event.getEventType(), "", "\t", KeyCode.TAB, event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown()));
        }
    }

    private void handleMoveTransaction(Transaction transaction, ObservableList<Transaction> transactions, int direction) {
        int currentIndex = transactions.indexOf(transaction);
        transactions.remove(transaction);
        if (direction < 0) {
            transactions.add(Math.max(0, currentIndex + direction), transaction);
        } else if (direction > 0) {
            transactions.add(Math.min(transactions.size(), currentIndex + direction), transaction);
        }
    }

    Transaction getTransaction() {
        return this.transaction;
    }
    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

}

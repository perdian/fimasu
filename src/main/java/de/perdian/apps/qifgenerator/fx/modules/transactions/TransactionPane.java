package de.perdian.apps.qifgenerator.fx.modules.transactions;

import de.perdian.apps.qifgenerator.fx.model.Transaction;
import de.perdian.apps.qifgenerator.fx.model.TransactionType;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.support.components.converters.DoubleStringConverter;
import de.perdian.apps.qifgenerator.fx.support.components.converters.LocalDateStringConverter;
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
        TextField currencyField = componentBuilder.createTextField(transaction.currencyProperty());
        currencyField.setPrefWidth(60);
        TextField titleField = componentBuilder.createTextField(transaction.titleProperty());
        GridPane.setHgrow(titleField, Priority.ALWAYS);

        GridPane topPane = new GridPane();
        topPane.add(buttonBox, 0, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("Type"), 1, 0, 1, 1);
        topPane.add(typeBox, 1, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("WKN"), 2, 0, 1, 1);
        topPane.add(wknField, 2, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("ISIN"), 3, 0, 1, 1);
        topPane.add(isinField, 3, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("Currency"), 4, 0, 1, 1);
        topPane.add(currencyField, 4, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("Title"), 5, 0, 1, 1);
        topPane.add(titleField, 5, 1, 1, 1);
        topPane.setHgap(4);
        topPane.setVgap(2);

        TextField bookingDateField = componentBuilder.createTextField(transaction.bookingDateProperty(), new LocalDateStringConverter());
        bookingDateField.setPrefWidth(85);
        TextField valutaDateField = componentBuilder.createTextField(transaction.valutaDateProperty(), new LocalDateStringConverter());
        valutaDateField.setPrefWidth(85);
        TextField numberOfSharesField = componentBuilder.createTextField(transaction.numberOfSharesProperty(), new DoubleStringConverter("0.00000"));
        numberOfSharesField.setPrefWidth(85);
        TextField marketPriceField = componentBuilder.createTextField(transaction.marketPriceProperty(), new DoubleStringConverter("0.00000"));
        marketPriceField.setPrefWidth(85);
        TextField marketValueField = componentBuilder.createTextField(transaction.marketValueProperty(), new DoubleStringConverter("0.00"));
        marketValueField.setDisable(true);
        marketValueField.setPrefWidth(75);
        TextField chargesField = componentBuilder.createTextField(transaction.chargesProperty(), new DoubleStringConverter("0.00"));
        chargesField.setPrefWidth(75);
        TextField financeTaxField = componentBuilder.createTextField(transaction.financeTaxProperty(), new DoubleStringConverter("0.00"));
        financeTaxField.setPrefWidth(75);
        TextField solidarityTaxField = componentBuilder.createTextField(transaction.solidarityTaxProperty(), new DoubleStringConverter("0.00"));
        solidarityTaxField.setPrefWidth(75);
        TextField totalValueField = componentBuilder.createTextField(transaction.totalValueProperty(), new DoubleStringConverter("0.00"));
        totalValueField.setPrefWidth(75);
        totalValueField.setDisable(true);
        GridPane.setHgrow(totalValueField, Priority.ALWAYS);
        GridPane bottomPane = new GridPane();
        bottomPane.setHgap(4);
        bottomPane.setVgap(2);
        bottomPane.add(componentBuilder.createLabel("Booking date"), 0, 0, 1, 1);
        bottomPane.add(bookingDateField, 0, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Valuta date"), 1, 0, 1, 1);
        bottomPane.add(valutaDateField, 1, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("# Shares"), 2, 0, 1, 1);
        bottomPane.add(numberOfSharesField, 2, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Market price"), 3, 0, 1, 1);
        bottomPane.add(marketPriceField, 3, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Market value"), 4, 0, 1, 1);
        bottomPane.add(marketValueField, 4, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Charges"), 5, 0, 1, 1);
        bottomPane.add(chargesField, 5, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Fin. tax"), 6, 0, 1, 1);
        bottomPane.add(financeTaxField, 6, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Sol. tax"), 7, 0, 1, 1);
        bottomPane.add(solidarityTaxField, 7, 1, 1, 1);
        bottomPane.add(componentBuilder.createLabel("Total"), 8, 0, 1, 1);
        bottomPane.add(totalValueField, 8, 1, 1, 1);

        this.getChildren().add(topPane);
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

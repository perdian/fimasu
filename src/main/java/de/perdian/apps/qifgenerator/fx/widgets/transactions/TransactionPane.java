package de.perdian.apps.qifgenerator.fx.widgets.transactions;

import java.util.List;
import java.util.Map;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.fx.support.converters.LocalDateStringConverter;
import de.perdian.apps.qifgenerator.model.Transaction;
import de.perdian.apps.qifgenerator.model.TransactionType;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

class TransactionPane extends VBox {

    TransactionPane(Transaction transaction, ObservableList<Transaction> transactions, Preferences preferences) {

        TransactionComponentBuilder componentBuilder = new TransactionComponentBuilder();
        this.getChildren().add(this.createFirstLinePane(componentBuilder, transaction, transactions));
        this.getChildren().add(this.createSecondLinePane(componentBuilder, transaction, transactions));

        this.setSpacing(8);

    }

    private Pane createFirstLinePane(TransactionComponentBuilder componentBuilder, Transaction transaction, ObservableList<Transaction> transactions) {

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

        TextField wknField = componentBuilder.createTextField(transaction.wknProperty()).width(65d).get();
        this.focusedProperty().addListener((o, oldValue, newValue) -> { if (Boolean.TRUE.equals(newValue)) { wknField.requestFocus(); } });

        GridPane firstLinePane = new GridPane();
        firstLinePane.setVgap(2);
        firstLinePane.setHgap(4);
        firstLinePane.add(buttonBox, 0, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Typ"), 1, 0, 1, 1);
        firstLinePane.add(componentBuilder.createComboBox(transaction.typeProperty(), TransactionType::toString, List.of(Map.entry("Buy", TransactionType.BUY), Map.entry("Sell", TransactionType.SELL))).width(70d).get(), 1, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("WKN"), 2, 0, 1, 1);
        firstLinePane.add(wknField, 2, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("ISIN"), 3, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.isinProperty()).width(115d).get(), 3, 1, 1, 1);
        firstLinePane.add(componentBuilder.createLabel("Title"), 4, 0, 1, 1);
        firstLinePane.add(componentBuilder.createTextField(transaction.titleProperty()).width(null).get(), 4, 1, 1, 1);
        return firstLinePane;

    }

    private Pane createSecondLinePane(TransactionComponentBuilder componentBuilder, Transaction transaction, ObservableList<Transaction> transactions) {
        GridPane secondLinePane = new GridPane();
        secondLinePane.add(componentBuilder.createLabel("Booking date"), 0, 0, 1, 1);
        secondLinePane.add(componentBuilder.createTextField(transaction.bookingDateProperty(), new LocalDateStringConverter()).width(100d).get(), 0, 1, 1, 1);
        secondLinePane.setVgap(2);
        secondLinePane.setHgap(4);
        return secondLinePane;
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

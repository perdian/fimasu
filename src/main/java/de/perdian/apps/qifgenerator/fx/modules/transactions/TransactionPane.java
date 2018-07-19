package de.perdian.apps.qifgenerator.fx.modules.transactions;

import de.perdian.apps.qifgenerator.fx.model.Transaction;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class TransactionPane extends VBox {

    private Transaction transaction = null;

    TransactionPane(Transaction transaction, ObservableList<Transaction> transactions, ComponentBuilder componentBuilder) {

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

        TextField wknField = componentBuilder.createTextField(transaction.wknProperty());
        wknField.setPrefWidth(80);
        GridPane topPane = new GridPane();
        topPane.add(buttonBox, 0, 1, 1, 1);
        topPane.add(componentBuilder.createLabel("WKN"), 1, 0, 1, 1);
        topPane.add(wknField, 1, 1, 1, 1);
        topPane.setHgap(4);
        topPane.setVgap(2);

        this.getChildren().add(topPane);
        this.setSpacing(4);
        this.setTransaction(transaction);

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

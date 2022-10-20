package de.perdian.apps.fimasu4.fx.modules.transactions;

import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;

import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import de.perdian.apps.fimasu4.model.types.Transaction;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

class TransactionPane extends GridPane {

    private Transaction transaction = null;

    TransactionPane(Transaction transaction, ObservableList<Transaction> allTransactions) {

        ComponentFactory componentFactory = new ComponentFactory();

        Label actionsLabel = componentFactory.createLabel(transaction.getType());
        Pane actionsPane = this.createActionsPane(transaction, allTransactions);

        this.add(actionsLabel, 0, 0, 1, 1);
        this.add(actionsPane, 0, 1, 1, 1);

        this.setPadding(new Insets(5, 5, 5, 5));
        this.setVgap(2);
        this.setTransaction(transaction);

    }

    private Pane createActionsPane(Transaction transaction, ObservableList<Transaction> allTransactions) {

        ComponentFactory componentFactory = new ComponentFactory();

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

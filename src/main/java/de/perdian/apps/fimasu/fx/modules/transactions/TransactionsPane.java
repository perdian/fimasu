package de.perdian.apps.fimasu.fx.modules.transactions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.fimasu.model.types.Transaction;
import de.perdian.apps.fimasu.model.types.TransactionGroup;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TransactionsPane extends BorderPane {

    public TransactionsPane(ObjectProperty<TransactionGroup> selectedTransactionGroup, ObservableList<File> importFiles) {

        this.setTop(new TransactionsToolBar(selectedTransactionGroup, importFiles));

        VBox transactionsPane = new VBox(10);
        ListChangeListener<Transaction> transactionChangeListener = (ListChangeListener.Change<? extends Transaction> change) -> {
            while (change.next()) {
                this.removeTransactions(change.getRemoved(), selectedTransactionGroup.getValue().getTransactions(), transactionsPane);
                this.addTransactions(change.getAddedSubList(), selectedTransactionGroup.getValue().getTransactions(), transactionsPane);
            }
        };
        selectedTransactionGroup.addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getTransactions().removeListener(transactionChangeListener);
            }
            this.reloadTransactions(newValue == null ? FXCollections.emptyObservableList() : newValue.getTransactions(), transactionsPane);
            if (newValue != null) {
                newValue.getTransactions().addListener(transactionChangeListener);
            }
        });
        if (selectedTransactionGroup.getValue() != null) {
            selectedTransactionGroup.getValue().getTransactions().addListener(transactionChangeListener);
            this.reloadTransactions(selectedTransactionGroup.getValue().getTransactions(), transactionsPane);
        }

        ScrollPane transactionsScrollPane  = new ScrollPane(transactionsPane);
        transactionsScrollPane.setStyle("-fx-background-color: transparent");
        transactionsScrollPane.setFitToWidth(true);
        transactionsScrollPane.setFocusTraversable(false);
        transactionsScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        transactionsScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setCenter(transactionsScrollPane);

    }

    private void reloadTransactions(ObservableList<Transaction> allTransactions, VBox targetPane) {
        targetPane.getChildren().clear();
        this.addTransactions(allTransactions, allTransactions, targetPane);
    }

    private void addTransactions(List<? extends Transaction> addTransactions, ObservableList<Transaction> allTransactions, VBox transactionsPane) {
        for (Transaction addTransaction : addTransactions) {
            int newTransactionIndex = allTransactions.indexOf(addTransaction);
            transactionsPane.getChildren().add(newTransactionIndex, new TransactionPane(addTransaction, allTransactions));
        }
    }

    private void removeTransactions(List<? extends Transaction> removeTransactions, ObservableList<Transaction> allTransactions, VBox transactionsPane) {
        for (Transaction removeTransaction : removeTransactions) {
            for (Node childNode : new ArrayList<>(transactionsPane.getChildren())) {
                if (childNode instanceof TransactionPane && removeTransaction.equals(((TransactionPane)childNode).getTransaction())) {
                    transactionsPane.getChildren().remove(childNode);
                }
            }
        }
    }

}

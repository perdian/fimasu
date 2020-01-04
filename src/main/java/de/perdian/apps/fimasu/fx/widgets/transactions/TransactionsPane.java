package de.perdian.apps.fimasu.fx.widgets.transactions;

import java.util.HashMap;
import java.util.Map;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class TransactionsPane extends BorderPane {

    public TransactionsPane(ObservableList<Transaction> transactions, ComponentBuilder componentBuilder, Preferences preferences) {

        VBox transactionsWrapper = new VBox(8);
        transactionsWrapper.setPadding(new Insets(8, 8, 8, 8));

        Map<Transaction, Pane> transactionPanesByTransaction = new HashMap<>();
        for (Transaction transaction : transactions) {
            Pane transactionPane = new TransactionPane(transaction, transactions, componentBuilder, preferences);
            transactionPanesByTransaction.put(transaction, transactionPane);
            transactionsWrapper.getChildren().add(transactionPane);
        }
        transactions.addListener((ListChangeListener.Change<? extends Transaction> change) -> {
            synchronized (transactionPanesByTransaction) {
                while (change.next()) {
                    change.getRemoved().forEach(removedTransaction -> {
                        Pane transactionPane = transactionPanesByTransaction.remove(removedTransaction);
                        if (transactionPane != null) {
                            transactionsWrapper.getChildren().remove(transactionPane);
                        }
                    });
                    change.getAddedSubList().forEach(addedTransaction -> {
                        int transactionIndex = transactions.indexOf(addedTransaction);
                        Pane transactionPane = new TransactionPane(addedTransaction, transactions, componentBuilder, preferences);
                        transactionPanesByTransaction.put(addedTransaction, transactionPane);
                        transactionsWrapper.getChildren().add(transactionIndex, transactionPane);
                        transactionPane.requestFocus();
                    });
                }
            }
        });
        ScrollPane transactionsWrapperScrollPane = new ScrollPane(transactionsWrapper);
        transactionsWrapperScrollPane.setFitToWidth(true);
        transactionsWrapperScrollPane.setFocusTraversable(false);
        transactionsWrapperScrollPane.setStyle("-fx-background-color: transparent");
        transactionsWrapperScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        transactionsWrapperScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setCenter(transactionsWrapperScrollPane);

    }

}
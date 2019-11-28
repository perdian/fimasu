package de.perdian.apps.qifgenerator_OLD.fx.modules.transactions;

import de.perdian.apps.qifgenerator_OLD.fx.model.Transaction;
import de.perdian.apps.qifgenerator_OLD.fx.support.components.ComponentBuilder;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

class TransactionGroupItemsPane extends BorderPane {

    TransactionGroupItemsPane(ObservableList<Transaction> transactions, ComponentBuilder componentBuilder) {

        VBox transactionsBox = new VBox();
        transactionsBox.setSpacing(25);
        transactionsBox.setMaxWidth(Double.MAX_VALUE);
        for (Transaction transaction : transactions) {
            transactionsBox.getChildren().add(new TransactionPane(transaction, transactions, componentBuilder));
        }

        ScrollPane transactionsScrollPane = new ScrollPane(transactionsBox);
        transactionsScrollPane.setBorder(null);
        transactionsScrollPane.setFitToWidth(true);
        transactionsScrollPane.setPadding(new Insets(8, 8, 8, 8));
        this.setCenter(transactionsScrollPane);

        transactions.addListener((ListChangeListener<Transaction>)event -> {
            while (event.next()) {
                for (Transaction removedTransaction : event.getRemoved()) {
                    for (Node transactionsBoxChild : transactionsBox.getChildren()) {
                        if (transactionsBoxChild instanceof TransactionPane) {
                            TransactionPane transactionPane = ((TransactionPane)transactionsBoxChild);
                            if (removedTransaction.equals(transactionPane.getTransaction())) {
                                Platform.runLater(() -> transactionsBox.getChildren().remove(transactionsBoxChild));
                            }
                        }
                    }
                }
                if (!event.getAddedSubList().isEmpty()) {
                    for (int i=0; i < event.getAddedSubList().size(); i++) {
                        Transaction newTransaction = event.getAddedSubList().get(i);
                        TransactionPane newTransactionPane = new TransactionPane(newTransaction, transactions, componentBuilder);
                        int targetIndex = transactions.indexOf(newTransaction);
                        Platform.runLater(() -> transactionsBox.getChildren().add(targetIndex, newTransactionPane));
                    }
                }
            }
        });

    }

}

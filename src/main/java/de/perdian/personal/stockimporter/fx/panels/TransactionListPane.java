package de.perdian.personal.stockimporter.fx.panels;

import de.perdian.personal.stockimporter.model.Transaction;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

class TransactionListPane extends BorderPane {

    TransactionListPane(ObservableList<Transaction> transactions) {

        VBox transactionsBox = new VBox();
        transactionsBox.setSpacing(25);
        transactionsBox.setMaxWidth(Double.MAX_VALUE);
        for (Transaction transaction : transactions) {
            transactionsBox.getChildren().add(new TransactionPane(transaction, e -> transactions.remove(transaction)));
        }
        ScrollPane transactionsScrollPane = new ScrollPane(transactionsBox);
        transactionsScrollPane.setFitToWidth(true);
        transactionsScrollPane.setPadding(new Insets(10, 10, 10, 10));

        Button addButton = new Button("Add transaction");
        addButton.setGraphic(new ImageView(new Image(TransactionListPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        addButton.setOnAction(event -> transactions.add(new Transaction()));
        addButton.setMaxWidth(Double.MAX_VALUE);
        Button exportButton = new Button("Export");
        exportButton.setGraphic(new ImageView(new Image(TransactionListPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        exportButton.setMaxWidth(Double.MAX_VALUE);
        VBox transactionsButtonBox = new VBox(addButton, exportButton);
        transactionsButtonBox.setSpacing(5);
        transactionsButtonBox.setPadding(new Insets(10, 5, 10, 5));

        this.setCenter(transactionsScrollPane);
        this.setRight(transactionsButtonBox);

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
                        TransactionPane newTransactionEditPane = new TransactionPane(newTransaction, e -> transactions.remove(newTransaction));
                        transactionsBox.getChildren().add(transactionsBox.getChildren().size(), newTransactionEditPane);
                    }
                }
            }
        });

    }

}

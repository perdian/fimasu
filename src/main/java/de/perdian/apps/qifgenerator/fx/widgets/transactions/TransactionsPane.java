package de.perdian.apps.qifgenerator.fx.widgets.transactions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.model.Transaction;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TransactionsPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(TransactionsPane.class);

    public TransactionsPane(ObservableList<Transaction> transactions, Preferences preferences) {

        Button addTransactionButton = new Button("Add transaction", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addTransactionButton.setOnAction(event -> transactions.add(new Transaction()));
        ToolBar buttonsToolBar = new ToolBar();
        buttonsToolBar.getItems().add(addTransactionButton);
        this.setTop(buttonsToolBar);

        log.debug("Initializing {} transactions", transactions.size());
        Map<Transaction, TransactionPane> transactionPanesByTransaction = new HashMap<>();
        VBox transactionsWrapper = new VBox(8);
        transactionsWrapper.setPadding(new Insets(8, 8, 8, 8));
        for (Transaction transaction : transactions) {
            TransactionPane transactionPane = new TransactionPane(transaction, transactions, preferences);
            transactionPane.setPadding(new Insets(0, 0, 12, 0));
            transactionPanesByTransaction.put(transaction, transactionPane);
            transactionsWrapper.getChildren().add(transactionPane);
        }
        transactions.addListener((ListChangeListener.Change<? extends Transaction> change) -> {
            synchronized (transactionPanesByTransaction) {
                while (change.next()) {
                    change.getRemoved().forEach(removedTransaction -> {
                        TransactionPane transactionPane = transactionPanesByTransaction.remove(removedTransaction);
                        if (transactionPane != null) {
                            transactionsWrapper.getChildren().remove(transactionPane);
                        }
                    });
                    change.getAddedSubList().forEach(addedTransaction -> {
                        int transactionIndex = transactions.indexOf(addedTransaction);
                        TransactionPane transactionPane = new TransactionPane(addedTransaction, transactions, preferences);
                        transactionPane.setPadding(new Insets(0, 0, 12, 0));
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

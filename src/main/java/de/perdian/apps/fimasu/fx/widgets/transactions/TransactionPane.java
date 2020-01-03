package de.perdian.apps.fimasu.fx.widgets.transactions;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

class TransactionPane extends BorderPane {

    TransactionPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {

        this.setPadding(new Insets(0, 0, 12, 0));

        TransactionDetailsPane transactionDetailsPane = TransactionDetailsPaneFactory.createTransactionDetailsPane(transaction, allTransactions, componentBuilder, preferences);
        transactionDetailsPane.setPadding(new Insets(2, 0, 0, 0));
        this.setCenter(transactionDetailsPane);

        TransactionActionsPane transactionActionsPane = new TransactionActionsPane(transactionDetailsPane.getTitle(), transaction, allTransactions, componentBuilder, preferences);
        transactionActionsPane.setPadding(new Insets(0, 0, 2, 0));
        this.setTop(transactionActionsPane);

    }

}

package de.perdian.apps.fimasu.fx.widgets.transactions;

import de.perdian.apps.fimasu.fx.widgets.transactions.impl.StockChangeTransactionPane;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;

public class TransactionPaneFactory {

    public static TransactionPane createTransactionPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        if (transaction instanceof StockChangeTransaction) {
            return new StockChangeTransactionPane((StockChangeTransaction)transaction, allTransactions, componentBuilder, preferences);
        } else {
            throw new IllegalArgumentException("Don't know how to build a TransactionPane for class: " + transaction.getClass().getName());
        }
    }

}

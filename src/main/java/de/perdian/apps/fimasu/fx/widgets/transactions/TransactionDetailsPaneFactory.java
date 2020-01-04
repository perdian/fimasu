package de.perdian.apps.fimasu.fx.widgets.transactions;

import de.perdian.apps.fimasu.fx.widgets.transactions.impl.PayoutTransactionDetailsPane;
import de.perdian.apps.fimasu.fx.widgets.transactions.impl.StockChangeTransactionDetailsPane;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.PayoutTransaction;
import de.perdian.apps.fimasu.model.impl.transactions.StockChangeTransaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;

public class TransactionDetailsPaneFactory {

    public static TransactionDetailsPane createTransactionDetailsPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
        if (transaction instanceof StockChangeTransaction) {
            return new StockChangeTransactionDetailsPane((StockChangeTransaction)transaction, allTransactions, componentBuilder, preferences);
        } else if (transaction instanceof PayoutTransaction) {
            return new PayoutTransactionDetailsPane((PayoutTransaction)transaction, allTransactions, componentBuilder, preferences);
        } else {
            throw new IllegalArgumentException("Don't know how to build a TransactionDetailsPane for class: " + transaction.getClass().getName());
        }
    }

}

package de.perdian.apps.fimasu.fx.widgets.transactions.impl;

import de.perdian.apps.fimasu.fx.widgets.transactions.TransactionDetailsPane;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.impl.transactions.PayoutTransaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;

public class PayoutTransactionDetailsPane extends TransactionDetailsPane {

    public PayoutTransactionDetailsPane(PayoutTransaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
    }

    @Override
    protected String getTitle() {
        return "Payout";
    }

}

package de.perdian.apps.fimasu.fx.widgets.transactions;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public abstract class TransactionPane extends VBox {

    protected TransactionPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {
    }

}

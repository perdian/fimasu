package de.perdian.apps.fimasu.fx.widgets.transactions;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

class TransactionPane extends VBox {

    TransactionPane(Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {

        this.setPadding(new Insets(0, 0, 12, 0));
        this.setSpacing(8);

        TransactionLinesFactory transactionLinesFactory = TransactionLinesFactory.forTransaction(transaction);
        transactionLinesFactory.createLines(transaction, allTransactions, componentBuilder, preferences).stream()
            .map(TransactionLine::toComponent)
            .forEach(this.getChildren()::add);

    }

}

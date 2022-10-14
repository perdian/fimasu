package de.perdian.apps.fimasu4.fx.modules.transactions;

import de.perdian.apps.fimasu4.model.TransactionGroup;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.BorderPane;

public class TransactionsPane extends BorderPane {

    public TransactionsPane(ObjectProperty<TransactionGroup> selectedTransactionGroup) {

        this.setTop(new TransactionsToolBar(selectedTransactionGroup));

    }

}

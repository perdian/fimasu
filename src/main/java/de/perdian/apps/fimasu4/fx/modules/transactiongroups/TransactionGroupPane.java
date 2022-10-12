package de.perdian.apps.fimasu4.fx.modules.transactiongroups;

import de.perdian.apps.fimasu4.model.TransactionGroup;
import javafx.scene.layout.BorderPane;

class TransactionGroupPane extends BorderPane {

    private TransactionGroup transactionGroup = null;

    TransactionGroupPane(TransactionGroup transactionGroup) {
        this.setTransactionGroup(transactionGroup);
    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}

package de.perdian.personal.stockqifgenerator.fx.actions;

import de.perdian.personal.stockqifgenerator.model.TransactionGroup;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ExportEventHandler implements EventHandler<ActionEvent> {

    private TransactionGroup transactionGroup = null;

    public ExportEventHandler(TransactionGroup transactionGroup) {
        this.setTransactionGroup(transactionGroup);
    }

    @Override
    public void handle(ActionEvent event) {
        throw new UnsupportedOperationException();
    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}

package de.perdian.apps.fimasu.fx.modules.transactions.actions;

import de.perdian.apps.fimasu.model.types.Transaction;
import de.perdian.apps.fimasu.model.types.TransactionGroup;
import de.perdian.apps.fimasu.model.types.TransactionType;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AddTransactionEventHandler implements EventHandler<ActionEvent> {

    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;
    private TransactionType transactionType = null;

    public AddTransactionEventHandler(ObjectProperty<TransactionGroup> selectedTransactionGroup, TransactionType transactionType) {
        this.setSelectedTransactionGroup(selectedTransactionGroup);
        this.setTransactionType(transactionType);
    }

    @Override
    public void handle(ActionEvent event) {
        TransactionGroup transactionGroup = this.getSelectedTransactionGroup().getValue();
        if (transactionGroup != null) {
            Transaction transaction = new Transaction();
            transaction.getType().setValue(this.getTransactionType());
            transactionGroup.getTransactions().add(transaction);
        }
    }

    private ObjectProperty<TransactionGroup> getSelectedTransactionGroup() {
        return this.selectedTransactionGroup;
    }
    private void setSelectedTransactionGroup(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.selectedTransactionGroup = selectedTransactionGroup;
    }

    private TransactionType getTransactionType() {
        return this.transactionType;
    }
    private void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

}

package de.perdian.apps.fimasu4.fx.modules.transactions;

import java.util.function.Supplier;

import de.perdian.apps.fimasu4.model.Transaction;
import de.perdian.apps.fimasu4.model.TransactionGroup;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AddTransactionEventHandler implements EventHandler<ActionEvent> {

    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;
    private Supplier<Transaction> transactionSupplier = null;

    public AddTransactionEventHandler(ObjectProperty<TransactionGroup> selectedTransactionGroup, Supplier<Transaction> transactionSupplier) {
        this.setSelectedTransactionGroup(selectedTransactionGroup);
        this.setTransactionSupplier(transactionSupplier);
    }

    @Override
    public void handle(ActionEvent event) {
        TransactionGroup transactionGroup = this.getSelectedTransactionGroup().getValue();
        if (transactionGroup != null) {
            transactionGroup.getTransactions().add(this.getTransactionSupplier().get());
        }
    }

    private ObjectProperty<TransactionGroup> getSelectedTransactionGroup() {
        return this.selectedTransactionGroup;
    }
    private void setSelectedTransactionGroup(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.selectedTransactionGroup = selectedTransactionGroup;
    }

    private Supplier<Transaction> getTransactionSupplier() {
        return this.transactionSupplier;
    }
    private void setTransactionSupplier(Supplier<Transaction> transactionSupplier) {
        this.transactionSupplier = transactionSupplier;
    }

}

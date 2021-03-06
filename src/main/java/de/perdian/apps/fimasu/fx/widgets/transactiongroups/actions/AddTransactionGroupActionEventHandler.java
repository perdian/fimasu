package de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions;

import de.perdian.apps.fimasu.model.TransactionGroup;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AddTransactionGroupActionEventHandler implements EventHandler<ActionEvent> {

    private ObservableList<TransactionGroup> transactionGroups = null;

    public AddTransactionGroupActionEventHandler(ObservableList<TransactionGroup> transactionGroups) {
        this.setTransactionGroups(transactionGroups);
    }

    @Override
    public void handle(ActionEvent event) {
        TransactionGroup newTransactionGroup = new TransactionGroup();
        newTransactionGroup.getTitle().setValue("New transaction group");
        this.getTransactionGroups().add(newTransactionGroup);
    }

    public ObservableList<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }
    public void setTransactionGroups(ObservableList<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

}

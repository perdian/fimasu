package de.perdian.apps.fimasu4.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionGroupModel {

    private ObservableList<TransactionGroup> transactionGroups = null;
    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;

    TransactionGroupModel() {
        this.setTransactionGroups(FXCollections.observableArrayList(new TransactionGroup()));
        this.setSelectedTransactionGroup(new SimpleObjectProperty<>());
    }

    public ObservableList<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }
    private void setTransactionGroups(ObservableList<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

    public ObjectProperty<TransactionGroup> getSelectedTransactionGroup() {
        return this.selectedTransactionGroup;
    }
    private void setSelectedTransactionGroup(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.selectedTransactionGroup = selectedTransactionGroup;
    }

}

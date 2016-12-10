package de.perdian.personal.stockimporter.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class StockModel {

    static final long serialVersionUID = 1L;

    private final ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();
    private final List<ChangeListener<StockModel>> changeListeners = new ArrayList<>();

    StockModel() {
        ChangeListener<TransactionGroup> transactionGroupChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.transactionGroupsProperty().addListener((ListChangeListener<TransactionGroup>)event -> {
            while (event.next()) {
                for (TransactionGroup removedTransactionGroup : event.getRemoved()) {
                    removedTransactionGroup.removeChangeListener(transactionGroupChangeListener);
                }
                for (TransactionGroup addedTransactionGroup : event.getAddedSubList()) {
                    addedTransactionGroup.addChangeListener(transactionGroupChangeListener);
                }
            }
            this.fireChange();
        });
    }

    public ObservableList<TransactionGroup> transactionGroupsProperty() {
        return this.transactionGroups;
    }

    void fireChange() {
        for (ChangeListener<StockModel> changeListener : this.changeListeners) {
            changeListener.changed(null, this, this);
        }
    }
    void addChangeListener(ChangeListener<StockModel> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<StockModel> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}

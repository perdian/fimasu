package de.perdian.personal.stockqifgenerator.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class StockQifGeneratorModel {

    static final long serialVersionUID = 1L;

    private final ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();
    private final List<ChangeListener<StockQifGeneratorModel>> changeListeners = new ArrayList<>();

    StockQifGeneratorModel() {
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
        for (ChangeListener<StockQifGeneratorModel> changeListener : this.changeListeners) {
            changeListener.changed(null, this, this);
        }
    }
    void addChangeListener(ChangeListener<StockQifGeneratorModel> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<StockQifGeneratorModel> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}

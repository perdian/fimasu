package de.perdian.apps.qifgenerator.model;

import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class QifGeneratorModel {

    static final long serialVersionUID = 1L;

    private final ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();
    private final List<ChangeListener<QifGeneratorModel>> changeListeners = new ArrayList<>();

    QifGeneratorModel() {
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
        for (ChangeListener<QifGeneratorModel> changeListener : this.changeListeners) {
            changeListener.changed(null, this, this);
        }
    }
    void addChangeListener(ChangeListener<QifGeneratorModel> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<QifGeneratorModel> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}

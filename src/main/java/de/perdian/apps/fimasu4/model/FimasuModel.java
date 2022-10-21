package de.perdian.apps.fimasu4.model;

import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class FimasuModel {

    private ObservableList<TransactionGroup> transactionGroups = null;
    private List<ChangeListener<Object>> changeListeners = null;
    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;

    public FimasuModel() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        ChangeListener<Object> delegatingChangeListener = (o, oldValue, newValue) -> {
            for (ChangeListener<Object> delegeeChangeListener : changeListeners) {
                delegeeChangeListener.changed(o, oldValue, newValue);
            }
        };
        this.setChangeListeners(changeListeners);

        ObjectProperty<TransactionGroup> selectedTransactionGroup = new SimpleObjectProperty<>();
        selectedTransactionGroup.addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getSelected().setValue(false);
            }
            if (newValue != null) {
                newValue.getSelected().setValue(true);
            }
        });
        this.setSelectedTransactionGroup(selectedTransactionGroup);

        ObservableList<TransactionGroup> transactionGroups = FXCollections.observableArrayList();
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                for (TransactionGroup addedTransactionGroup : change.getAddedSubList()) {
                    addedTransactionGroup.addChangeListener(delegatingChangeListener);
                }
            }
            delegatingChangeListener.changed(null, null, null);
        });
        this.setTransactionGroups(transactionGroups);

    }

    public ObservableList<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }
    private void setTransactionGroups(ObservableList<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    private List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<Object>> changeListeners) {
        this.changeListeners = changeListeners;
    }

    public ObjectProperty<TransactionGroup> getSelectedTransactionGroup() {
        return this.selectedTransactionGroup;
    }
    private void setSelectedTransactionGroup(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.selectedTransactionGroup = selectedTransactionGroup;
    }

}

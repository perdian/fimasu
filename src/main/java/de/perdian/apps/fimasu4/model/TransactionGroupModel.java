package de.perdian.apps.fimasu4.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu4.model.persistence.Values;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransactionGroupModel {

    private ObservableList<TransactionGroup> transactionGroups = null;
    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;
    private List<ChangeListener<Object>> changeListeners = null;

    TransactionGroupModel() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        ChangeListener<Object> delegatingChangeListener = (o, oldValue, newValue) -> {
            for (ChangeListener<Object> delegeeChangeListener : changeListeners) {
                delegeeChangeListener.changed(o, oldValue, newValue);
            }
        };
        this.setChangeListeners(changeListeners);

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

        ObjectProperty<TransactionGroup> selectedTransactionGroup = new SimpleObjectProperty<>();
        selectedTransactionGroup.addListener(delegatingChangeListener);
        this.setSelectedTransactionGroup(selectedTransactionGroup);

    }

    public void readValues(Values sourceValues) {

        List<Values> transactionGroupValuesList = sourceValues.getChildren("transactionGroup");
        if (transactionGroupValuesList != null) {
            List<TransactionGroup> transactionGroups = new ArrayList<>();
            for (Values transactionGroupValues : transactionGroupValuesList) {
                TransactionGroup transactionGroup = new TransactionGroup();
                transactionGroup.readValues(transactionGroupValues);
                transactionGroups.add(transactionGroup);
            }
            this.getTransactionGroups().setAll(transactionGroups);
        }

        String selectedTransactionGroupTitle = sourceValues.getAttribute("selectedTransactionGroupTitle", null);
        if (StringUtils.isNotEmpty(selectedTransactionGroupTitle)) {
            for (TransactionGroup transactionGroup : this.getTransactionGroups()) {
                if (selectedTransactionGroupTitle.equals(transactionGroup.getTitle().getValue())) {
                    this.getSelectedTransactionGroup().setValue(transactionGroup);
                }
            }
        }

    }

    public Values writeValues() {

        TransactionGroup selectedTransactionGroup = this.getSelectedTransactionGroup().getValue();
        String selectedTransactionGroupTitle = selectedTransactionGroup == null ? null : selectedTransactionGroup.getTitle().getValue();

        Values values = new Values();
        values.setAttribute("selectedTransactionGroupTitle", selectedTransactionGroupTitle);
        values.addChildren("transactionGroup", this.getTransactionGroups().stream().filter(tg -> tg.getPersistent().getValue()).map(TransactionGroup::writeValues).toList());
        return values;

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

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    private List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<Object>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}

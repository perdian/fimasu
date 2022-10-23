package de.perdian.apps.fimasu.model.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransactionGroup implements Serializable {

    static final long serialVersionUID = 1L;

    private StringProperty title = null;
    private StringProperty bankAccountName = null;
    private StringProperty exportFileName = null;
    private BooleanProperty persistent = null;
    private BooleanProperty selected = null;
    private ObservableList<Transaction> transactions = null;
    private List<ChangeListener<Object>> changeListeners = null;

    public TransactionGroup() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        ChangeListener<Object> delegatingChangeListener = (o, oldValue, newValue) -> {
            for (ChangeListener<Object> delegeeChangeListener : changeListeners) {
                delegeeChangeListener.changed(o, oldValue, newValue);
            }
        };
        this.setChangeListeners(changeListeners);

        StringProperty title = new SimpleStringProperty("New transaction group");
        title.addListener(delegatingChangeListener);
        this.setTitle(title);

        StringProperty bankAccountName = new SimpleStringProperty();
        bankAccountName.addListener(delegatingChangeListener);
        this.setBankAccountName(bankAccountName);

        StringProperty exportFileName = new SimpleStringProperty();
        exportFileName.addListener(delegatingChangeListener);
        this.setExportFileName(exportFileName);

        BooleanProperty persistent = new SimpleBooleanProperty(false);
        persistent.addListener(delegatingChangeListener);
        this.setPersistent(persistent);

        BooleanProperty selected = new SimpleBooleanProperty(false);
        selected.addListener(delegatingChangeListener);
        this.setSelected(selected);

        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        transactions.addListener((ListChangeListener.Change<? extends Transaction> change) -> {
            while (change.next()) {
                for (Transaction addedTransaction : change.getAddedSubList()) {
                    addedTransaction.addChangeListener(delegatingChangeListener);
                }
            }
            delegatingChangeListener.changed(null, null, null);
        });
        this.setTransactions(transactions);

    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("title", this.getTitle().getValue());
        toStringBuilder.append("bankAccountName", this.getBankAccountName().getValue());
        toStringBuilder.append("exportFileName", this.getExportFileName().getValue());
        toStringBuilder.append("persistent", this.getPersistent().getValue());
        toStringBuilder.append("transactions", this.getTransactions());
        return toStringBuilder.toString();
    }

    public StringProperty getTitle() {
        return this.title;
    }
    private void setTitle(StringProperty title) {
        this.title = title;
    }

    public StringProperty getBankAccountName() {
        return this.bankAccountName;
    }
    private void setBankAccountName(StringProperty bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public StringProperty getExportFileName() {
        return this.exportFileName;
    }
    private void setExportFileName(StringProperty exportFileName) {
        this.exportFileName = exportFileName;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }
    private void setPersistent(BooleanProperty persistent) {
        this.persistent = persistent;
    }

    public BooleanProperty getSelected() {
        return this.selected;
    }
    private void setSelected(BooleanProperty selected) {
        this.selected = selected;
    }

    public ObservableList<Transaction> getTransactions() {
        return this.transactions;
    }
    private void setTransactions(ObservableList<Transaction> transactions) {
        this.transactions = transactions;
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

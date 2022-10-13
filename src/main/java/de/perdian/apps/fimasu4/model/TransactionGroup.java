package de.perdian.apps.fimasu4.model;

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

    private StringProperty title = new SimpleStringProperty("New transaction group");
    private StringProperty bankAccountName = new SimpleStringProperty();
    private StringProperty targetFileName = new SimpleStringProperty();
    private BooleanProperty persistent = new SimpleBooleanProperty(false);
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
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

        StringProperty targetFileName = new SimpleStringProperty();
        targetFileName.addListener(delegatingChangeListener);
        this.setTargetFileName(targetFileName);

        BooleanProperty persistent = new SimpleBooleanProperty(false);
        persistent.addListener(delegatingChangeListener);
        this.setPersistent(persistent);

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
        toStringBuilder.append("targetFileName", this.getTargetFileName().getValue());
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

    public StringProperty getTargetFileName() {
        return this.targetFileName;
    }
    private void setTargetFileName(StringProperty targetFileName) {
        this.targetFileName = targetFileName;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }
    private void setPersistent(BooleanProperty persistent) {
        this.persistent = persistent;
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

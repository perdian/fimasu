package de.perdian.apps.fimasu4.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionGroup implements Serializable {

    static final long serialVersionUID = 1L;

    private StringProperty title = new SimpleStringProperty("New transaction group");
    private StringProperty bankAccountName = new SimpleStringProperty();
    private StringProperty targetFileName = new SimpleStringProperty();
    private BooleanProperty persistent = new SimpleBooleanProperty(false);
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

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
    void setTitle(StringProperty title) {
        this.title = title;
    }

    public StringProperty getBankAccountName() {
        return this.bankAccountName;
    }
    void setBankAccountName(StringProperty bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public StringProperty getTargetFileName() {
        return this.targetFileName;
    }
    public void setTargetFileName(StringProperty targetFileName) {
        this.targetFileName = targetFileName;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }
    void setPersistent(BooleanProperty persistent) {
        this.persistent = persistent;
    }

    public ObservableList<Transaction> getTransactions() {
        return this.transactions;
    }
    void setTransactions(ObservableList<Transaction> transactions) {
        this.transactions = transactions;
    }

}

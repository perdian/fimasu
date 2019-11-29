package de.perdian.apps.qifgenerator.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TransactionGroup implements Externalizable {

    static final long serialVersionUID = 1L;

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty account = new SimpleStringProperty();
    private final StringProperty targetFilePath = new SimpleStringProperty();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final List<ChangeListener<TransactionGroup>> changeListeners = new CopyOnWriteArrayList<>();

    public TransactionGroup() {
        this.getTransactions().addListener((ListChangeListener<Transaction>)event -> {
            while (event.next()) {
                for (Transaction addedTransaction : event.getAddedSubList()) {
                    addedTransaction.addChangeListener((o, oldValue, newValue) -> this.fireChange());
                    addedTransaction.getBookingDate().addListener((o, oldValue, newValue) -> this.onChangeTransactionDate(addedTransaction, newValue));
                }
            }
            this.fireChange();
        });
        this.getTitle().addListener((x, oldValue, newValue) -> this.fireChange());
        this.getTitle().addListener((x, oldValue, newValue) -> this.fireChange());
        this.getTitle().addListener((x, oldValue, newValue) -> this.fireChange());
    }

    private void fireChange() {
        for (ChangeListener<TransactionGroup> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }

    private void onChangeTransactionDate(Transaction transaction, LocalDate newValue) {
        int indexOfTransaction = this.getTransactions().indexOf(transaction);
        if (indexOfTransaction == 0) {
            for (int i=1; i < this.getTransactions().size(); i++) {
                Transaction needleTransaction = this.getTransactions().get(i);
                if (needleTransaction.getBookingDate().getValue() == null && needleTransaction.getValutaDate().getValue() == null) {
                    needleTransaction.getBookingDate().setValue(transaction.getBookingDate().getValue());
                    needleTransaction.getValutaDate().setValue(transaction.getValutaDate().getValue());
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(StringUtils.defaultIfEmpty(this.getTitle().getValue(), ""));
        out.writeObject(new ArrayList<>(this.getTransactions()));
        out.writeUTF(StringUtils.defaultIfEmpty(this.getAccount().getValue(), ""));
        out.writeUTF(StringUtils.defaultIfEmpty(this.getTargetFilePath().getValue(), ""));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.getTitle().setValue(in.readUTF());
        this.getTransactions().setAll((List<Transaction>)in.readObject());
        this.getAccount().setValue(in.readUTF());
        this.getTargetFilePath().setValue(in.readUTF());
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public StringProperty getAccount() {
        return this.account;
    }

    public StringProperty getTargetFilePath() {
        return this.targetFilePath;
    }

    public ObservableList<Transaction> getTransactions() {
        return this.transactions;
    }

    public boolean addChangeListener(ChangeListener<TransactionGroup> changeListener) {
        return this.getChangeListeners().add(changeListener);
    }
    public boolean removeChangeListener(ChangeListener<TransactionGroup> changeListener) {
        return this.getChangeListeners().remove(changeListener);
    }
    private List<ChangeListener<TransactionGroup>> getChangeListeners() {
        return this.changeListeners;
    }


}

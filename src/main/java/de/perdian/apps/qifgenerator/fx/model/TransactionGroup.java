package de.perdian.apps.qifgenerator.fx.model;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
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
    private final Property<File> targetFile = new SimpleObjectProperty<>();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final List<ChangeListener<TransactionGroup>> changeListeners = new ArrayList<>();

    public TransactionGroup() {
        this.initListeners();
    }

    private void initListeners() {
        ChangeListener<Transaction> transactionChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.transactionsProperty().addListener((ListChangeListener<Transaction>)event -> {
            while (event.next()) {
                for (Transaction removedTransaction : event.getRemoved()) {
                    removedTransaction.removeChangeListener(transactionChangeListener);
                }
                for (Transaction addedTransaction : event.getAddedSubList()) {
                    addedTransaction.addChangeListener(transactionChangeListener);
                }
            }
            this.fireChange();
        });
        this.titleProperty().addListener((x, oldValue, newValue) -> this.fireChange());
        this.accountProperty().addListener((x, oldValue, newValue) -> this.fireChange());
        this.targetFileProperty().addListener((x, oldValue, newValue) -> this.fireChange());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.titleProperty().getValue());
        out.writeObject(new ArrayList<>(this.transactionsProperty()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.titleProperty().setValue(in.readUTF());
        this.transactionsProperty().setAll((List<Transaction>)in.readObject());
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public StringProperty accountProperty() {
        return this.account;
    }

    public Property<File> targetFileProperty() {
        return this.targetFile;
    }

    public ObservableList<Transaction> transactionsProperty() {
        return this.transactions;
    }

    void fireChange() {
        for (ChangeListener<TransactionGroup> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }
    public void addChangeListener(ChangeListener<TransactionGroup> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    public void removeChangeListener(ChangeListener<TransactionGroup> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}

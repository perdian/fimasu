package de.perdian.apps.qifgenerator_OLD.fx.model;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
        this.transactionsProperty().addListener((ListChangeListener<Transaction>)event -> {
            while (event.next()) {
                for (Transaction addedTransaction : event.getAddedSubList()) {
                    addedTransaction.addChangeListener((o, oldValue, newValue) -> this.fireChange());
                    addedTransaction.bookingDateProperty().addListener((o, oldValue, newValue) -> this.onChangeTransactionDate(addedTransaction, newValue));
                }
            }
            this.fireChange();
        });
        this.titleProperty().addListener((x, oldValue, newValue) -> this.fireChange());
        this.accountProperty().addListener((x, oldValue, newValue) -> this.fireChange());
        this.targetFileProperty().addListener((x, oldValue, newValue) -> this.fireChange());
    }

    private void onChangeTransactionDate(Transaction transaction, LocalDate newValue) {
        int indexOfTransaction = this.transactionsProperty().indexOf(transaction);
        if (indexOfTransaction == 0) {
            for (int i=1; i < this.transactionsProperty().size(); i++) {
                Transaction needleTransaction = this.transactionsProperty().get(i);
                if (needleTransaction.bookingDateProperty().getValue() == null && needleTransaction.valutaDateProperty().getValue() == null) {
                    needleTransaction.bookingDateProperty().setValue(transaction.bookingDateProperty().getValue());
                    needleTransaction.valutaDateProperty().setValue(transaction.valutaDateProperty().getValue());
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.titleProperty().getValue());
        out.writeObject(new ArrayList<>(this.transactionsProperty()));
        out.writeObject(this.accountProperty().getValue());
        out.writeObject(this.targetFileProperty().getValue() == null ? null : this.targetFileProperty().getValue().getAbsolutePath());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.titleProperty().setValue(in.readUTF());
        this.transactionsProperty().setAll((List<Transaction>)in.readObject());
        this.accountProperty().setValue((String)in.readObject());
        String targetFilePath = (String)in.readObject();
        this.targetFileProperty().setValue(StringUtils.isEmpty(targetFilePath) ? null : new File(targetFilePath));
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

    private void fireChange() {
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

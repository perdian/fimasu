package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions;

import java.io.File;

import de.perdian.apps.qifgenerator.model.TransactionGroup;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ParseFromFilesActionEventHandler implements EventHandler<ActionEvent> {

    private TransactionGroup transactionGroup = null;
    private ObservableList<File> files = null;

    public ParseFromFilesActionEventHandler(TransactionGroup transactionGroup, ObservableList<File> files) {
        this.setTransactionGroup(transactionGroup);
        this.setFiles(files);
    }

    @Override
    public void handle(ActionEvent event) {
    }

    private TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

    private ObservableList<File> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<File> files) {
        this.files = files;
    }

}

package de.perdian.apps.fimasu4.fx.modules.transactions.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu4.model.parsers.TransactionParser;
import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ImportTransactionsFromFilesEventHandler implements EventHandler<ActionEvent> {

    private ObservableList<File> sourceFiles = null;
    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;

    public ImportTransactionsFromFilesEventHandler(ObservableList<File> sourceFiles, ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.setSourceFiles(sourceFiles);
        this.setSelectedTransactionGroup(selectedTransactionGroup);
    }

    @Override
    public void handle(ActionEvent event) {
        if (!this.getSourceFiles().isEmpty()) {
            List<Transaction> importedTransactions = new ArrayList<>();
            for (File importFile : this.getSourceFiles()) {
                List<Transaction> transactionsFromFile = TransactionParser.parseTransactions(importFile);
                transactionsFromFile.stream()
                    .filter(transaction -> StringUtils.isNotEmpty(transaction.getStockIdentifier().getIsin().getValue()))
                    .forEach(importedTransactions::add);
            }
            List<Transaction> targetTransactions = this.getSelectedTransactionGroup().getValue().getTransactions();
            for (Transaction importedTransaction : importedTransactions) {
                Transaction targetTransaction = targetTransactions.stream()
                    .filter(transaction -> transaction.getStockIdentifier().equals(importedTransaction.getStockIdentifier()))
                    .findFirst()
                    .orElse(null);
                if (targetTransaction != null) {
                    targetTransactions.set(targetTransactions.indexOf(targetTransaction), importedTransaction);
                } else {
                    targetTransactions.add(importedTransaction);
                }
            }
        }
    }

    private ObservableList<File> getSourceFiles() {
        return this.sourceFiles;
    }
    private void setSourceFiles(ObservableList<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    private ObjectProperty<TransactionGroup> getSelectedTransactionGroup() {
        return this.selectedTransactionGroup;
    }
    private void setSelectedTransactionGroup(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.selectedTransactionGroup = selectedTransactionGroup;
    }

}

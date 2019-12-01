package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.qifgenerator.model.Transaction;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.model.TransactionParser;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ImportFromFilesActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<TransactionGroup> transactionGroupSupplier = null;
    private ObservableList<File> files = null;

    public ImportFromFilesActionEventHandler(Supplier<TransactionGroup> transactionGroupSupplier, ObservableList<File> files) {
        this.setTransactionGroupSupplier(transactionGroupSupplier);
        this.setFiles(files);
    }

    @Override
    public void handle(ActionEvent event) {
        TransactionGroup transactionGroup = this.getTransactionGroupSupplier().get();
        Map<String, Transaction> importedTransactionsByIsin = new TreeMap<>();
        for (File file : this.getFiles()) {
            List<Transaction> transactionsFromFile = TransactionParser.parseTransactions(file);
            transactionsFromFile.stream().filter(transaction -> StringUtils.isNotEmpty(transaction.getIsin().getValue())).forEach(transaction -> importedTransactionsByIsin.put(transaction.getIsin().getValue(), transaction));
        }
        transactionGroup.getTransactions().forEach(transaction -> {
            try {
                Transaction importedTransaction = StringUtils.isEmpty(transaction.getIsin().getValue()) ? null : importedTransactionsByIsin.get(transaction.getIsin().getValue());
                if (importedTransaction != null) {
                    importedTransaction.copyValuesInto(transaction);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Supplier<TransactionGroup> getTransactionGroupSupplier() {
        return this.transactionGroupSupplier;
    }
    private void setTransactionGroupSupplier(Supplier<TransactionGroup> transactionGroupSupplier) {
        this.transactionGroupSupplier = transactionGroupSupplier;
    }

    private ObservableList<File> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<File> files) {
        this.files = files;
    }

}

package de.perdian.apps.fimasu.fx.widgets.transactions.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu4.model.parsers.TransactionParser;
import de.perdian.commons.fx.execution.GuiExecutor;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ImportFromFilesActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ImportFromFilesActionEventHandler.class);

    private ObservableList<Transaction> transactions = null;
    private ObservableList<File> files = null;
    private GuiExecutor guiExecutor = null;

    public ImportFromFilesActionEventHandler(ObservableList<Transaction> transactions, ObservableList<File> files, GuiExecutor guiExecutor) {
        this.setTransactions(transactions);
        this.setFiles(files);
        this.setGuiExecutor(guiExecutor);
    }

    @Override
    public synchronized void handle(ActionEvent event) {
        if (!this.getFiles().isEmpty()) {
            this.getGuiExecutor().execute(progressController -> {
                List<Transaction> importedTransactions = new ArrayList<>();
                for (int fileIndex = 0; fileIndex < this.getFiles().size(); fileIndex++) {
                    File file = this.getFiles().get(fileIndex);
                    progressController.updateProgress("Analyzing file: " + file.getName(), (double)fileIndex / (double)this.getFiles().size());
                    List<Transaction> transactionsFromFile = TransactionParser.parseTransactions(file);
                    transactionsFromFile.stream()
                        .filter(transaction -> StringUtils.isNotEmpty(transaction.getIsin().getValue()))
                        .forEach(importedTransactions::add);
                }
                for (int transactionIndex = 0; transactionIndex < importedTransactions.size(); transactionIndex++) {
                    progressController.updateProgress("", (double)transactionIndex / (double)importedTransactions.size());
                    this.updateImportedTransaction(importedTransactions.get(transactionIndex));
                }
            });
        }
    }

    private void updateImportedTransaction(Transaction importedTransaction) {
        synchronized (this.getTransactions()) {
            Transaction targetTransaction = this.getTransactions().stream().filter(transaction -> Objects.equals(transaction.getIsin().getValue(), importedTransaction.getIsin().getValue())).findFirst().orElse(null);
            Platform.runLater(() -> {
                if (targetTransaction == null) {
                    synchronized (this.getTransactions()) {
                        this.getTransactions().add(importedTransaction);
                    }
                } else {
                    try {
                        importedTransaction.copyValuesInto(targetTransaction);
                    } catch (Exception e) {
                        log.info("Cannot update transaction: {}", targetTransaction, e);
                    }
                }
            });
        }
    }

    private ObservableList<Transaction> getTransactions() {
        return this.transactions;
    }
    private void setTransactions(ObservableList<Transaction> transactions) {
        this.transactions = transactions;
    }

    private ObservableList<File> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<File> files) {
        this.files = files;
    }

    private GuiExecutor getGuiExecutor() {
        return this.guiExecutor;
    }
    private void setGuiExecutor(GuiExecutor guiExecutor) {
        this.guiExecutor = guiExecutor;
    }

}

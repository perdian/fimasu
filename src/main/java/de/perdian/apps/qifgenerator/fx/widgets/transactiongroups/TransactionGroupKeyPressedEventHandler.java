package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import java.io.File;
import java.util.function.Supplier;

import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions.ExportAsQifActionEventHandler;
import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions.ImportFromFilesActionEventHandler;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.commons.fx.execution.GuiExecutor;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class TransactionGroupKeyPressedEventHandler implements EventHandler<KeyEvent> {

    private Supplier<TransactionGroup> transactionGroupSupplier = null;
    private ObservableList<File> files = null;
    private GuiExecutor guiExecutor = null;

    TransactionGroupKeyPressedEventHandler(Supplier<TransactionGroup> transactionGroupSupplier, ObservableList<File> files, GuiExecutor guiExecutor) {
        this.setTransactionGroupSupplier(transactionGroupSupplier);
        this.setFiles(files);
        this.setGuiExecutor(guiExecutor);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.E && event.isMetaDown()) {
            new ExportAsQifActionEventHandler(this.getTransactionGroupSupplier(), this.getGuiExecutor()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        } else if (event.getCode() == KeyCode.I && event.isMetaDown()) {
            new ImportFromFilesActionEventHandler(this.getTransactionGroupSupplier(), this.getFiles(), this.getGuiExecutor()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        }
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

    private GuiExecutor getGuiExecutor() {
        return this.guiExecutor;
    }
    private void setGuiExecutor(GuiExecutor guiExecutor) {
        this.guiExecutor = guiExecutor;
    }

}

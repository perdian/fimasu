package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.qifgenerator.model.transactions.TransactionGroup;
import de.perdian.apps.qifgenerator.model.transactions.TransactionGroupSerializer;
import de.perdian.commons.fx.execution.GuiExecutor;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class RestoreTransactionGroupsActionEventHandler implements EventHandler<ActionEvent> {

    private Collection<TransactionGroup> transactionGroups = null;
    private StringProperty backupDirectory = null;
    private GuiExecutor guiExecutor = null;

    public RestoreTransactionGroupsActionEventHandler(Collection<TransactionGroup> transactionGroups, StringProperty backupDirectory, GuiExecutor guiExecutor) {
        this.setTransactionGroups(transactionGroups);
        this.setBackupDirectory(backupDirectory);
        this.setGuiExecutor(guiExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select source file");
        String backupDirectoryValue = this.getBackupDirectory().getValue();
        if (StringUtils.isNotEmpty(backupDirectoryValue)) {
            File backupDirectory = new File(backupDirectoryValue);
            if (backupDirectory.exists()) {
                fileChooser.setInitialDirectory(backupDirectory);
            }
        }
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            this.getBackupDirectory().setValue(selectedFile.getParentFile().getAbsolutePath());
            this.getGuiExecutor().execute(progressController -> {
                progressController.updateProgress("Restore transaction groups from backup", null);
                try (InputStream fileStream = new BufferedInputStream(new FileInputStream(selectedFile))) {
                    List<TransactionGroup> transactionGroups = TransactionGroupSerializer.deserializeTransactionGroups(fileStream);
                    this.getTransactionGroups().addAll(transactionGroups);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Restore completed");
                        alert.setHeaderText(null);
                        alert.setContentText("Restored " + transactionGroups.size() + " transaction groups");
                        alert.getDialogPane().getStylesheets().add("META-INF/stylesheets/qifgenerator.css");
                        alert.showAndWait();
                    });
                }
            });
        }
    }

    private Collection<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }
    private void setTransactionGroups(Collection<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

    private StringProperty getBackupDirectory() {
        return this.backupDirectory;
    }
    private void setBackupDirectory(StringProperty backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    private GuiExecutor getGuiExecutor() {
        return this.guiExecutor;
    }
    private void setGuiExecutor(GuiExecutor guiExecutor) {
        this.guiExecutor = guiExecutor;
    }

}

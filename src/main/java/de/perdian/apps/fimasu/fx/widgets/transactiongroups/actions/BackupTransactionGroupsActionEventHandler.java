package de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions;

import java.util.Collection;

import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.commons.fx.execution.GuiExecutor;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class BackupTransactionGroupsActionEventHandler implements EventHandler<ActionEvent> {

    private Collection<TransactionGroup> transactionGroups = null;
    private StringProperty backupDirectory = null;
    private GuiExecutor guiExecutor = null;

    public BackupTransactionGroupsActionEventHandler(Collection<TransactionGroup> transactionGroups, StringProperty backupDirectory, GuiExecutor guiExecutor) {
        this.setTransactionGroups(transactionGroups);
        this.setBackupDirectory(backupDirectory);
        this.setGuiExecutor(guiExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select target file");
//        String backupDirectoryValue = this.getBackupDirectory().getValue();
//        if (StringUtils.isNotEmpty(backupDirectoryValue)) {
//            File backupDirectory = new File(backupDirectoryValue);
//            if (backupDirectory.exists()) {
//                fileChooser.setInitialDirectory(backupDirectory);
//            }
//        }
//        File selectedFile = fileChooser.showSaveDialog(null);
//        if (selectedFile != null) {
//            this.getBackupDirectory().setValue(selectedFile.getParentFile().getAbsolutePath());
//            this.getGuiExecutor().execute(progressController -> {
//                progressController.updateProgress("Backup transaction groups", null);
//                try (OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(selectedFile))) {
//                    TransactionGroupSerializer.serializeTransactionGroups(this.getTransactionGroups(), fileStream);
//                    fileStream.flush();
//                    Platform.runLater(() -> {
//                        Alert alert = new Alert(AlertType.INFORMATION);
//                        alert.setTitle("Backup completed");
//                        alert.setHeaderText(null);
//                        alert.setContentText("Backup completed");
//                        alert.getDialogPane().getStylesheets().add("META-INF/stylesheets/fimasu.css");
//                        alert.showAndWait();
//                    });
//                }
//            });
//        }
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

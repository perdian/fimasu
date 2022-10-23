package de.perdian.apps.fimasu.fx.modules.transactions.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.types.TransactionGroup;
import de.perdian.apps.fimasu.quicken.RecordList;
import de.perdian.apps.fimasu.quicken.RecordListBuilder;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExportTransactionsAsQifEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExportTransactionsAsQifEventHandler.class);

    private ObjectProperty<TransactionGroup> selectedTransactionGroup = null;

    public ExportTransactionsAsQifEventHandler(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.setSelectedTransactionGroup(selectedTransactionGroup);
    }

    @Override
    public void handle(ActionEvent event) {

        TransactionGroup transactionGroup = this.getSelectedTransactionGroup().getValue();
        RecordListBuilder qifQecordListBuilder = new RecordListBuilder();
        RecordList qifRecordList = qifQecordListBuilder.buildRecordList(transactionGroup);
        String qifString = qifRecordList.toQifString();

        String qifFileLocation = transactionGroup.getExportFileName().getValue();
        if (StringUtils.isEmpty(qifFileLocation)) {
            this.showAlert(AlertType.ERROR, "Export failed", "No target file specified!");
        } else {
            try {
                File qifFile = new File(qifFileLocation);
                log.info("Exporting transaction group into file at '{}': {}", qifFile.getAbsolutePath(), transactionGroup);
                FileUtils.write(qifFile, qifString, "UTF-8");
                this.showAlert(AlertType.INFORMATION, "Export completed", "Exported transactions into file: " + qifFile.getName());
            } catch (Exception e) {
                this.showAlert(AlertType.ERROR, "Export failed", "Export failed: " + e.toString());
            }
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private ObjectProperty<TransactionGroup> getSelectedTransactionGroup() {
        return this.selectedTransactionGroup;
    }
    private void setSelectedTransactionGroup(ObjectProperty<TransactionGroup> selectedTransactionGroup) {
        this.selectedTransactionGroup = selectedTransactionGroup;
    }

}

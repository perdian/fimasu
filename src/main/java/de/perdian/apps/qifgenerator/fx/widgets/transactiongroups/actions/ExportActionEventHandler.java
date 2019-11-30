package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.quicken.QifContentGenerator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExportActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExportActionEventHandler.class);

    private TransactionGroup transactionGroup = null;

    public ExportActionEventHandler(TransactionGroup transactionGroup) {
        this.setTransactionGroup(transactionGroup);
    }

    @Override
    public void handle(ActionEvent event) {
        QifContentGenerator qifContentGenerator = new QifContentGenerator();
        String qifContent = qifContentGenerator.generate(this.getTransactionGroup());
        String qifFileLocation = this.getTransactionGroup().getTargetFilePath().getValue();
        if (StringUtils.isEmpty(qifFileLocation)) {
            this.showAlert(AlertType.ERROR, "Export failed", "No target file specified!");
        } else {
            try {
                File qifFile = new File(qifFileLocation);
                log.info("Exporting transaction group into file at '{}': {}", qifFile.getAbsolutePath(), this.getTransactionGroup());
                FileUtils.write(qifFile, qifContent, "UTF-8");
                this.showAlert(AlertType.INFORMATION, "Export completed", "Exported transactions into file: " + qifFile.getName());
            } catch (Exception e) {
                this.showAlert(AlertType.ERROR, "Export failed", "Export failed: " + e.toString());
            }
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add("META-INF/stylesheets/qifgenerator.css");
        alert.showAndWait();
    }

    private TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}

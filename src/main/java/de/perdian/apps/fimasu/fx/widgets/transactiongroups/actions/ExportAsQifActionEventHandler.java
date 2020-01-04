package de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.support.quicken.QIFWriter;
import de.perdian.commons.fx.execution.GuiExecutor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExportAsQifActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExportAsQifActionEventHandler.class);

    private TransactionGroup transactionGroup = null;
    private GuiExecutor guiExecutor = null;

    public ExportAsQifActionEventHandler(TransactionGroup transactionGroup, GuiExecutor guiExecutor) {
        this.setTransactionGroup(transactionGroup);
        this.setGuiExecutor(guiExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        this.getGuiExecutor().execute(progressController -> {
            QIFWriter qifWriter = new QIFWriter();
            this.getTransactionGroup().appendToQIF(qifWriter);
            String qifContent = qifWriter.toOutput();
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
        });
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().getStylesheets().add("META-INF/stylesheets/fimasu.css");
            alert.showAndWait();
        });
    }

    private TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

    private GuiExecutor getGuiExecutor() {
        return this.guiExecutor;
    }
    private void setGuiExecutor(GuiExecutor guiExecutor) {
        this.guiExecutor = guiExecutor;
    }

}

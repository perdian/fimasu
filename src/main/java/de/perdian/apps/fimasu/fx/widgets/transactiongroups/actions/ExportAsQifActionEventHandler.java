package de.perdian.apps.fimasu.fx.widgets.transactiongroups.actions;

import java.io.File;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.export.quicken.QIFWriter;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.commons.fx.execution.GuiExecutor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExportAsQifActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExportAsQifActionEventHandler.class);

    private Supplier<TransactionGroup> transactionGroupSupplier = null;
    private GuiExecutor guiExecutor = null;

    public ExportAsQifActionEventHandler(Supplier<TransactionGroup> transactionGroupSupplier, GuiExecutor guiExecutor) {
        this.setTransactionGroupSupplier(transactionGroupSupplier);
        this.setGuiExecutor(guiExecutor);
    }

    @Override
    public void handle(ActionEvent event) {
        TransactionGroup transactionGroup = this.getTransactionGroupSupplier().get();
        this.getGuiExecutor().execute(progressController -> {
            QIFWriter qifWriter = new QIFWriter();
            transactionGroup.appendToQIF(qifWriter);
            String qifContent = qifWriter.toOutput();
            String qifFileLocation = transactionGroup.getTargetFilePath().getValue();
            if (StringUtils.isEmpty(qifFileLocation)) {
                this.showAlert(AlertType.ERROR, "Export failed", "No target file specified!");
            } else {
                try {
                    File qifFile = new File(qifFileLocation);
                    log.info("Exporting transaction group into file at '{}': {}", qifFile.getAbsolutePath(), transactionGroup);
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

    private Supplier<TransactionGroup> getTransactionGroupSupplier() {
        return this.transactionGroupSupplier;
    }
    private void setTransactionGroupSupplier(Supplier<TransactionGroup> transactionGroupSupplier) {
        this.transactionGroupSupplier = transactionGroupSupplier;
    }

    private GuiExecutor getGuiExecutor() {
        return this.guiExecutor;
    }
    private void setGuiExecutor(GuiExecutor guiExecutor) {
        this.guiExecutor = guiExecutor;
    }

}

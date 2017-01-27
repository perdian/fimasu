package de.perdian.apps.qifgenerator.fx.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.quicken.QifContentGenerator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

public class ExportEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExportEventHandler.class);
    private TransactionGroup transactionGroup = null;

    public ExportEventHandler(TransactionGroup transactionGroup) {
        this.setTransactionGroup(transactionGroup);
    }

    @Override
    public void handle(ActionEvent event) {

        File currentTargetFile = this.getTransactionGroup().targetFileProperty().getValue();
        File currentDirectory = currentTargetFile == null ? null : currentTargetFile.getParentFile();

        FileChooser targetFileChooser = new FileChooser();
        if (currentDirectory != null) {
            targetFileChooser.setInitialDirectory(currentDirectory);
        }
        targetFileChooser.setTitle("Select target file");

        File selectedFile = targetFileChooser.showSaveDialog(((Node)event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".qif")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".qif");
            }
            this.getTransactionGroup().targetFileProperty().setValue(selectedFile);
            new Thread(() -> this.handleExport(this.getTransactionGroup().targetFileProperty().getValue())).start();
        }

    }

    private void handleExport(File selectedFile) {
        try {

            QifContentGenerator contentGenerator = new QifContentGenerator();
            String content = contentGenerator.generate(this.getTransactionGroup());

            if (!selectedFile.getParentFile().exists()) {
                log.debug("Creating directory: {}", selectedFile.getParentFile().getAbsolutePath());
                selectedFile.getParentFile().mkdirs();
            }
            log.debug("Writing QIF output into file: {}", selectedFile.getAbsolutePath());
            FileUtils.write(selectedFile, content, "UTF-8");
            log.debug("QIF output written into: {}", selectedFile.getAbsolutePath());

            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Export generation completed");
                alert.setHeaderText(null);
                alert.setContentText("Export file has been generated into:\n" + selectedFile.getAbsolutePath());
                alert.showAndWait();
            });

        } catch (Exception e) {
            log.warn("Cannot execute export", e);
            Platform.runLater(() -> {

                Label exceptionDetailLabel = new Label("The exception was:");
                TextArea exceptionStacktraceArea = new TextArea(ExceptionUtils.getStackTrace(e));
                exceptionStacktraceArea.setEditable(false);
                exceptionStacktraceArea.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(exceptionStacktraceArea, Priority.ALWAYS);

                GridPane exceptionDetailPane = new GridPane();
                exceptionDetailPane.setMaxWidth(Double.MAX_VALUE);
                exceptionDetailPane.add(exceptionDetailLabel, 0, 0);
                exceptionDetailPane.add(exceptionStacktraceArea, 0, 1);

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Exception occured");
                alert.setHeaderText("Exception occured during export file generation");
                alert.setContentText("Export cannot be performed!");
                alert.getDialogPane().setExpandableContent(exceptionDetailPane);
                alert.showAndWait();

            });
        }
    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}

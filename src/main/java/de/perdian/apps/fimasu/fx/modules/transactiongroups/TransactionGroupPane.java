package de.perdian.apps.fimasu.fx.modules.transactiongroups;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import de.perdian.apps.fimasu.fx.support.ComponentFactory;
import de.perdian.apps.fimasu.model.types.TransactionGroup;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

class TransactionGroupPane extends VBox {

    private TransactionGroup transactionGroup = null;

    TransactionGroupPane(TransactionGroup transactionGroup) {

        ComponentFactory componentFactory = new ComponentFactory();

        ToggleButton persistentButton = componentFactory.createToggleButton(transactionGroup.getPersistent(), MaterialDesignC.CONTENT_SAVE);
        persistentButton.setTooltip(new Tooltip("Persist transaction group"));

        TextField titleField = componentFactory.createTextField(transactionGroup.getTitle());
        titleField.setPrefWidth(400);
        Label titleLabel = componentFactory.createLabel("_Title");
        titleLabel.setLabelFor(titleField);

        TextField bankAccountNameField = componentFactory.createTextField(transactionGroup.getBankAccountName());
        GridPane.setHgrow(bankAccountNameField, Priority.ALWAYS);
        Label bankAccountNameLabel = componentFactory.createLabel("_Bank accout name");
        bankAccountNameLabel.setLabelFor(bankAccountNameField);

        TextField exportFileNameField = componentFactory.createTextField(transactionGroup.getExportFileName());
        Button exportFileNameButton = componentFactory.createButton(MaterialDesignF.FILE, action -> this.selectExportFile(exportFileNameField.textProperty()));
        exportFileNameButton.setTooltip(new Tooltip("Select export file"));
        GridPane.setHgrow(exportFileNameField, Priority.ALWAYS);
        Label exportFileNameLabel = componentFactory.createLabel("Export _file name");
        exportFileNameLabel.setLabelFor(exportFileNameField);

        GridPane firstLine = new GridPane();
        firstLine.setHgap(5);
        firstLine.add(persistentButton, 0, 1, 1, 1);
        firstLine.add(titleLabel, 1, 0, 1, 1);
        firstLine.add(titleField, 1, 1, 1, 1);
        firstLine.add(bankAccountNameLabel, 2, 0, 1, 1);
        firstLine.add(bankAccountNameField, 2, 1, 1, 1);

        GridPane secondLine = new GridPane();
        secondLine.setHgap(5);
        secondLine.add(exportFileNameLabel, 0, 0, 2, 1);
        secondLine.add(exportFileNameField, 0, 1, 1, 1);
        secondLine.add(exportFileNameButton, 1, 1, 1, 1);

        this.setSpacing(10);
        this.getChildren().addAll(firstLine, secondLine);
        this.setTransactionGroup(transactionGroup);

    }

    private void selectExportFile(StringProperty targetProperty) {

        String currentFileValue = targetProperty.getValue();
        File currentFile = StringUtils.isEmpty(currentFileValue) ? null : new File(currentFileValue);
        File currentDirectory = currentFile == null ? null : currentFile.getParentFile();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select target file");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("QIF files", "*.qif"));
        if (currentDirectory != null && currentDirectory.exists()) {
            fileChooser.setInitialDirectory(currentDirectory);
        }
        if (currentFile != null) {
            fileChooser.setInitialFileName(currentFile.getName());
        }
        File selectedFile = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            targetProperty.setValue(selectedFile.getAbsolutePath());
        }

    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}

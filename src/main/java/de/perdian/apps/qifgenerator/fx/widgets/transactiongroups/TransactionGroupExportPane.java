package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

class TransactionGroupExportPane extends VBox {

    TransactionGroupExportPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder, Preferences preferences) {

        Button exportButton = new Button("Export", new FontAwesomeIconView(FontAwesomeIcon.FILE));
        ToolBar buttonToolbar = new ToolBar(exportButton);
        this.getChildren().add(buttonToolbar);

        GridPane firstRowPane = new GridPane();
        firstRowPane.add(componentBuilder.createLabel("Transaction group title"), 0, 0, 1, 1);
        firstRowPane.add(componentBuilder.createTextField(transactionGroup.getTitle()).width(200d).focusTraversable(false).get(), 0, 1, 1, 1);
        firstRowPane.add(componentBuilder.createLabel("Account name"), 1, 0, 1, 1);
        firstRowPane.add(componentBuilder.createTextField(transactionGroup.getAccount()).focusTraversable(false).get(), 1, 1, 1, 1);
        firstRowPane.setHgap(4);
        firstRowPane.setVgap(2);
        firstRowPane.setPadding(new Insets(8, 8, 4, 8));
        this.getChildren().add(firstRowPane);

        StringProperty initialDirectory = preferences.getStringProperty("transactionGroup.export.initialDirectory", System.getProperty("user.home"));
        TextField fileField = componentBuilder.createTextField(transactionGroup.getTargetFilePath()).focusTraversable(false).get();
        GridPane.setHgrow(fileField, Priority.ALWAYS);
        Button fileSelectButton = new Button("Select", new FontAwesomeIconView(FontAwesomeIcon.HAND_POINTER_ALT));
        fileSelectButton.setOnAction(event -> {
            File currentlySelectedFile = StringUtils.isEmpty(transactionGroup.getTargetFilePath().getValue()) ? null : new File(transactionGroup.getTargetFilePath().getValue());
            String currentlySelectedDirectoryPath = currentlySelectedFile == null ? initialDirectory.getValue() : currentlySelectedFile.getParentFile().getAbsolutePath();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(currentlySelectedFile == null ? "transactions.qif" : currentlySelectedFile.getName());
            fileChooser.setInitialDirectory(StringUtils.isEmpty(currentlySelectedDirectoryPath) ? null : new File(currentlySelectedDirectoryPath));
            fileChooser.setTitle("Select target file");
            File selectedFile = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (selectedFile != null) {
                transactionGroup.getTargetFilePath().setValue(selectedFile.getAbsolutePath());
                initialDirectory.setValue(selectedFile.getParentFile().getAbsolutePath());
            }
        });
        GridPane secondRowPane = new GridPane();
        secondRowPane.add(componentBuilder.createLabel("Target file"), 0, 0, 2, 1);
        secondRowPane.add(fileField, 0, 1, 1, 1);
        secondRowPane.add(fileSelectButton, 1, 1, 1, 1);
        secondRowPane.setVgap(2);
        secondRowPane.setHgap(2);
        secondRowPane.setPadding(new Insets(4, 8, 8, 8));
        this.getChildren().add(secondRowPane);

    }

}

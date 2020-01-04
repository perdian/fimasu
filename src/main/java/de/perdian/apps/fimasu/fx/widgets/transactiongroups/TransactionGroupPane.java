package de.perdian.apps.fimasu.fx.widgets.transactiongroups;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.fx.widgets.transactions.TransactionsPane;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.execution.GuiExecutor;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

class TransactionGroupPane extends VBox {

    TransactionGroupPane(TransactionGroup transactionGroup, ObservableList<File> files, GuiExecutor guiExecutor, ComponentBuilder componentBuilder, Preferences preferences) {

        BorderPane groupPaneWrapper = new BorderPane();
        groupPaneWrapper.setPadding(new Insets(0));
        groupPaneWrapper.setCenter(new GroupPane(transactionGroup, componentBuilder, preferences));
        TitledPane dataTitledPane = new TitledPane("Transaction group", groupPaneWrapper);
        dataTitledPane.setCollapsible(false);

        BorderPane transactionsListPaneWrapper = new BorderPane();
        transactionsListPaneWrapper.setPadding(new Insets(0));
        transactionsListPaneWrapper.setTop(new TransactionGroupToolBar(transactionGroup, files, guiExecutor, componentBuilder, preferences));
        transactionsListPaneWrapper.setCenter(new TransactionsPane(transactionGroup.getTransactions(), componentBuilder, preferences));
        TitledPane transactionsTitledPane = new TitledPane("Transactions", transactionsListPaneWrapper);
        transactionsTitledPane.setCollapsible(false);
        transactionsTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(transactionsTitledPane, Priority.ALWAYS);

        this.setSpacing(8);
        this.setPadding(new Insets(8, 8, 8, 8));
        this.getChildren().addAll(dataTitledPane, transactionsTitledPane);

    }

    private static class GroupPane extends VBox {

        GroupPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder, Preferences preferences) {
            ToggleButton persistentToggleButon = new ToggleButton(null, new FontAwesomeIconView(FontAwesomeIcon.SAVE));
            persistentToggleButon.setTooltip(new Tooltip("Save transaction group when existing the application"));
            persistentToggleButon.selectedProperty().bindBidirectional(transactionGroup.getPersistent());
            GridPane firstRowPane = new GridPane();
            firstRowPane.add(persistentToggleButon, 0, 1, 1, 1);
            firstRowPane.add(componentBuilder.createLabel("Transaction group title"), 0, 0, 2, 1);
            firstRowPane.add(componentBuilder.createTextField(transactionGroup.getTitle()).width(200d).focusTraversable(false).get(), 1, 1, 1, 1);
            firstRowPane.add(componentBuilder.createLabel("Account name"), 2, 0, 1, 1);
            firstRowPane.add(componentBuilder.createTextField(transactionGroup.getAccount()).focusTraversable(false).get(), 2, 1, 1, 1);
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

}

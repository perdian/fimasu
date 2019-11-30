package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions.ExportActionEventHandler;
import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.actions.ParseFromFilesActionEventHandler;
import de.perdian.apps.qifgenerator.fx.widgets.transactions.TransactionPane;
import de.perdian.apps.qifgenerator.model.Transaction;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

class TransactionGroupPane extends VBox {

    TransactionGroupPane(TransactionGroup transactionGroup, ObservableList<File> files, ComponentBuilder componentBuilder, Preferences preferences) {

        TransactionsListPane transactionsListPane = new TransactionsListPane(transactionGroup.getTransactions(), componentBuilder, preferences);
        transactionsListPane.setTop(new ActionsToolBar(transactionGroup, files, componentBuilder, preferences));
        TitledPane transactionsTitledPane = new TitledPane("Transactions", transactionsListPane);
        transactionsTitledPane.setCollapsible(false);
        transactionsTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(transactionsTitledPane, Priority.ALWAYS);

        DataPane dataPane = new DataPane(transactionGroup, componentBuilder, preferences);
        TitledPane dataTitledPane = new TitledPane("Data", dataPane);
        dataTitledPane.setCollapsible(false);

        this.setSpacing(8);
        this.setPadding(new Insets(8, 8, 8, 8));
        this.getChildren().addAll(transactionsTitledPane, dataTitledPane);

    }

    private static class ActionsToolBar extends ToolBar {

        private ActionsToolBar(TransactionGroup transactionGroup, ObservableList<File> files, ComponentBuilder componentBuilder, Preferences preferences) {

            Button addTransactionButton = new Button("Add transaction", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
            addTransactionButton.setOnAction(event -> transactionGroup.getTransactions().add(new Transaction()));
            this.getItems().add(addTransactionButton);

            Button exportButton = new Button("Export", new FontAwesomeIconView(FontAwesomeIcon.SAVE));
            exportButton.setOnAction(new ExportActionEventHandler(transactionGroup));
            Button importFromFilesButton = new Button("Import from files", new FontAwesomeIconView(FontAwesomeIcon.FILE));
            importFromFilesButton.setOnAction(new ParseFromFilesActionEventHandler(transactionGroup, files));
            this.getItems().add(exportButton);

        }

    }

    private static class TransactionsListPane extends BorderPane {

        private TransactionsListPane(ObservableList<Transaction> transactions, ComponentBuilder componentBuilder, Preferences preferences) {

            Map<Transaction, TransactionPane> transactionPanesByTransaction = new HashMap<>();
            VBox transactionsWrapper = new VBox(8);
            transactionsWrapper.setPadding(new Insets(8, 8, 8, 8));
            for (Transaction transaction : transactions) {
                TransactionPane transactionPane = new TransactionPane(transaction, transactions, componentBuilder, preferences);
                transactionPane.setPadding(new Insets(0, 0, 12, 0));
                transactionPanesByTransaction.put(transaction, transactionPane);
                transactionsWrapper.getChildren().add(transactionPane);
            }
            transactions.addListener((ListChangeListener.Change<? extends Transaction> change) -> {
                synchronized (transactionPanesByTransaction) {
                    while (change.next()) {
                        change.getRemoved().forEach(removedTransaction -> {
                            TransactionPane transactionPane = transactionPanesByTransaction.remove(removedTransaction);
                            if (transactionPane != null) {
                                transactionsWrapper.getChildren().remove(transactionPane);
                            }
                        });
                        change.getAddedSubList().forEach(addedTransaction -> {
                            int transactionIndex = transactions.indexOf(addedTransaction);
                            TransactionPane transactionPane = new TransactionPane(addedTransaction, transactions, componentBuilder, preferences);
                            transactionPane.setPadding(new Insets(0, 0, 12, 0));
                            transactionPanesByTransaction.put(addedTransaction, transactionPane);
                            transactionsWrapper.getChildren().add(transactionIndex, transactionPane);
                            transactionPane.requestFocus();
                        });
                    }
                }
            });
            ScrollPane transactionsWrapperScrollPane = new ScrollPane(transactionsWrapper);
            transactionsWrapperScrollPane.setFitToWidth(true);
            transactionsWrapperScrollPane.setFocusTraversable(false);
            transactionsWrapperScrollPane.setStyle("-fx-background-color: transparent");
            transactionsWrapperScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            transactionsWrapperScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
            this.setCenter(transactionsWrapperScrollPane);

        }

    }

    private static class DataPane extends VBox {

        DataPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder, Preferences preferences) {

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

}

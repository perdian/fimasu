package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class TransactionGroupsPane extends BorderPane {

    public TransactionGroupsPane(ObservableList<TransactionGroup> transactionGroups, ComponentBuilder componentBuilder, Preferences preferences) {

        MenuItem createTransactionGroupItem = new MenuItem("Add transaction group");
        createTransactionGroupItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        createTransactionGroupItem.setOnAction(event -> this.onCreateButtonClicked(transactionGroups, this.getScene().getWindow()));

        IntegerProperty selectedTabIndexProperty = preferences.getIntegerProperty("transactions.selectedTabIndex", 0);
        TabPane tabPane = new TabPane();
        for (TransactionGroup transactionGroup : transactionGroups) {
            tabPane.getTabs().add(new TransactionGroupTab(transactionGroups, transactionGroup, componentBuilder, preferences));
        }
        tabPane.setContextMenu(new ContextMenu(createTransactionGroupItem));
        tabPane.getSelectionModel().select(selectedTabIndexProperty.getValue());
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                for (TransactionGroup newGroup : change.getAddedSubList()) {
                    Tab newTab = new TransactionGroupTab(transactionGroups, newGroup, componentBuilder, preferences);
                    tabPane.getTabs().add(newTab);
                    tabPane.getSelectionModel().select(newTab);
                }
            }
        });
        selectedTabIndexProperty.bind(tabPane.getSelectionModel().selectedIndexProperty());

        this.setCenter(tabPane);

    }

    private void onCreateButtonClicked(ObservableList<TransactionGroup> transactionGroups, Window window) {

        TransactionGroup newTransactionGroup = new TransactionGroup();
        TextField titleField = new TextField();
        titleField.textProperty().bindBidirectional(newTransactionGroup.getTitle());
        titleField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(titleField, Priority.ALWAYS);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Title"), 0, 0);
        grid.add(titleField, 1, 0);
        Platform.runLater(() -> titleField.requestFocus());

        ButtonType saveButtonType = new ButtonType("Save", ButtonData.FINISH);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        Dialog<TransactionGroup> dialog = new Dialog<>();
        dialog.setTitle("Create new transaction grouo");
        dialog.setHeaderText("Define the data for the new transaction group");
        dialog.setResultConverter(dialogButton -> dialogButton == saveButtonType ? newTransactionGroup : null);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add("META-INF/stylesheets/qifgenerator.css");
        dialog.showAndWait().ifPresent(transactionGroup -> transactionGroups.add(transactionGroup));

    }

    static class TransactionGroupTab extends Tab {

        TransactionGroupTab(ObservableList<TransactionGroup> transactionGroups, TransactionGroup transactionGroup, ComponentBuilder componentBuilder, Preferences preferences) {
            this.textProperty().bind(transactionGroup.getTitle());
            this.setOnCloseRequest(event -> {
                if (transactionGroups.size() > 1) {
                    Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Delete transaction group");
                    confirmationAlert.setHeaderText("Delete transaction group");
                    confirmationAlert.setContentText("Really delete the transaction group?");
                    if (confirmationAlert.showAndWait().get().equals(ButtonType.OK)) {
                        transactionGroups.remove(transactionGroup);
                    } else {
                        event.consume();
                    }
                } else {
                    event.consume();
                }
            });
            this.setContent(new TransactionGroupPane(transactionGroup, componentBuilder, preferences));
        }

    }

}

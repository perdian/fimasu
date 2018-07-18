package de.perdian.apps.qifgenerator.fxnew.modules.transactions;

import de.perdian.apps.qifgenerator.fx.QifGeneratorModelPane;
import de.perdian.apps.qifgenerator.fxnew.QifGeneratorPreferences;
import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class TransactionsPane extends BorderPane {

    public TransactionsPane(QifGeneratorPreferences preferences) {

        ObservableList<TransactionGroup> transactionGroups = preferences.getTransactionGroups();
        MenuItem createTransactionGroupItem = new MenuItem("Add transaction group");
        createTransactionGroupItem.setGraphic(new ImageView(new Image(QifGeneratorModelPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        createTransactionGroupItem.setOnAction(event -> {
            TransactionGroup newTransactionGroup = this.handleCreateButtonClicked(this.getScene().getWindow());
            if (newTransactionGroup != null) {
                transactionGroups.add(newTransactionGroup);
            }
        });
        TabPane tabPane = new TabPane();
        for (TransactionGroup transactionGroup : transactionGroups) {
            tabPane.getTabs().add(new TransactionGroupTab(transactionGroup, transactionGroups));
        }
        tabPane.setContextMenu(new ContextMenu(createTransactionGroupItem));
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                for (TransactionGroup newGroup : change.getAddedSubList()) {
                    tabPane.getTabs().add(new TransactionGroupTab(newGroup, transactionGroups));
                }
            }
        });

        TitledPane titledPane = new TitledPane("Transactions", tabPane);
        titledPane.setCollapsible(false);
        titledPane.setMaxWidth(Double.MAX_VALUE);
        titledPane.setMaxHeight(Double.MAX_VALUE);
        this.setCenter(titledPane);

    }

    private TransactionGroup handleCreateButtonClicked(Window parentWindow) {

        TransactionGroup newTransactionGroup = new TransactionGroup(null);
        TextField titleField = new TextField();
        titleField.textProperty().bindBidirectional(newTransactionGroup.titleProperty());
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
        return dialog.showAndWait().orElse(null);

    }

}

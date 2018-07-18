package de.perdian.apps.qifgenerator.fx;

import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import de.perdian.apps.qifgenerator.model.QifGeneratorModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class QifGeneratorModelPane extends BorderPane {

    public QifGeneratorModelPane(QifGeneratorModel model) {

        MenuItem createTransactionGroupItem = new MenuItem("Add transaction group");
        createTransactionGroupItem.setGraphic(new ImageView(new Image(QifGeneratorModelPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        createTransactionGroupItem.setOnAction(event -> this.handleCreateButtonClicked(model, this.getScene().getWindow()));
        ContextMenu contextMenu = new ContextMenu(createTransactionGroupItem);

        TabPane tabPane = new TabPane();
        for (TransactionGroup transactionGroup : model.transactionGroupsProperty()) {
            tabPane.getTabs().add(this.createTransactionGroupTab(transactionGroup, model));
        }
        tabPane.setContextMenu(contextMenu);

        this.setCenter(tabPane);

        model.transactionGroupsProperty().addListener((ListChangeListener<TransactionGroup>)(event -> this.handleModelListChanged(event, tabPane, model)));

    }

    private void handleCreateButtonClicked(QifGeneratorModel model, Window parentWindow) {

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

        TransactionGroup resultTransactionGroup = dialog.showAndWait().orElse(null);
        if (resultTransactionGroup != null) {
            model.transactionGroupsProperty().add(resultTransactionGroup);
        }

    }

    private void handleModelListChanged(Change<? extends TransactionGroup> event, TabPane tabPane, QifGeneratorModel model) {
        while (event.next()) {
            for (TransactionGroup newTransactionGroup : event.getAddedSubList()) {
                TransactionGroupTab newTransactionGroupTab = this.createTransactionGroupTab(newTransactionGroup, model);
                tabPane.getTabs().add(newTransactionGroupTab);
                tabPane.getSelectionModel().select(newTransactionGroupTab);
            }
        }
    }

    private TransactionGroupTab createTransactionGroupTab(TransactionGroup transactionGroup, QifGeneratorModel model) {
        TransactionGroupTab newTransactionGroupTab = new TransactionGroupTab(transactionGroup, model);
        newTransactionGroupTab.setOnCloseRequest(event -> {
            if (model.transactionGroupsProperty().size() == 1) {
                event.consume();
            } else {
                model.transactionGroupsProperty().remove(transactionGroup);
            }
        });
        return newTransactionGroupTab;
    }

}

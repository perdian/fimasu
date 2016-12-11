package de.perdian.personal.stockqifgenerator.fx;

import java.util.Optional;

import de.perdian.personal.stockqifgenerator.model.TransactionGroup;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

class TransactionGroupCreationDialog extends BorderPane {

    static TransactionGroup createTransactionGroup(Window parentWindow) {

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
        Optional<TransactionGroup> dialogResult = dialog.showAndWait();

        return dialogResult.orElse(null);

    }

}

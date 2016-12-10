package de.perdian.personal.stockimporter.fx.panels;

import java.util.Optional;

import de.perdian.personal.stockimporter.model.BuyingTime;
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

class BuyingTimeCreationDialog extends BorderPane {

    static BuyingTime createBuyingTime(Window parentWindow) {

        BuyingTime newBuyingTime = new BuyingTime();
        TextField titleField = new TextField();
        titleField.textProperty().bindBidirectional(newBuyingTime.titleProperty());
        titleField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(titleField, Priority.ALWAYS);
        GridPane newBuyingTimeGrid = new GridPane();
        newBuyingTimeGrid.setHgap(10);
        newBuyingTimeGrid.setVgap(10);
        newBuyingTimeGrid.add(new Label("Title"), 0, 0);
        newBuyingTimeGrid.add(titleField, 1, 0);
        Platform.runLater(() -> titleField.requestFocus());

        ButtonType saveButtonType = new ButtonType("Save", ButtonData.FINISH);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        Dialog<BuyingTime> dialog = new Dialog<>();
        dialog.setTitle("Create new buying time");
        dialog.setHeaderText("Define the data for the new buying time");
        dialog.setResultConverter(dialogButton -> dialogButton == saveButtonType ? newBuyingTime : null);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().setContent(newBuyingTimeGrid);
        Optional<BuyingTime> dialogResult = dialog.showAndWait();

        return dialogResult.orElse(null);

    }

}

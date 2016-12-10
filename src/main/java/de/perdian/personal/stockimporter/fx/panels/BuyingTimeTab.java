package de.perdian.personal.stockimporter.fx.panels;

import de.perdian.personal.stockimporter.model.BuyingTime;
import de.perdian.personal.stockimporter.model.StockModel;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

class BuyingTimeTab extends Tab {

    BuyingTimeTab(BuyingTime buyingTime, StockModel model) {

        Button generateExportDocumentButton = new Button("Generate export document");
        ToolBar buttonBar = new ToolBar();
        buttonBar.setPadding(new Insets(10, 10, 10, 10));
        buttonBar.getItems().add(generateExportDocumentButton);

        BorderPane buyingTimePane = new BorderPane();
        buyingTimePane.setCenter(new ShareListPane(buyingTime.sharesProperty()));
        buyingTimePane.setBottom(buttonBar);

        this.textProperty().bind(buyingTime.titleProperty());
        this.setOnCloseRequest(event -> {
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete buying time");
            confirmationAlert.setHeaderText("Delete buying time");
            confirmationAlert.setContentText("Really delete the buying time?");
            if (confirmationAlert.showAndWait().get().equals(ButtonType.OK)) {
                model.buyingTimesProperty().remove(buyingTime);
            } else {
                event.consume();
            }
        });
        this.setContent(buyingTimePane);
    }

}

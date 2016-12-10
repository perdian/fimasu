package de.perdian.personal.stockimporter.fx.panels;

import de.perdian.personal.stockimporter.model.BuyingTime;
import de.perdian.personal.stockimporter.model.StockModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class StockModelPane extends BorderPane {

    public StockModelPane(StockModel model) {

        Button createBuyingTimeButton = new Button("New buying time");
        createBuyingTimeButton.setOnAction(event -> this.handleCreateButtonClicked(model, ((Node)event.getSource()).getScene().getWindow()));
        ToolBar titleBar = new ToolBar();
        titleBar.getItems().add(createBuyingTimeButton);

        TabPane tabPane = new TabPane();
        for (BuyingTime buyingTime : model.buyingTimesProperty()) {
            tabPane.getTabs().add(new BuyingTimeTab(buyingTime, model));
        }

        this.setTop(titleBar);
        this.setCenter(tabPane);

        model.buyingTimesProperty().addListener((ListChangeListener<BuyingTime>)(event -> this.handleModelListChanged(event, tabPane, model)));

    }

    private void handleCreateButtonClicked(StockModel model, Window parentWindow) {
        BuyingTime newBuyingTime = BuyingTimeCreationDialog.createBuyingTime(parentWindow);
        if (newBuyingTime != null) {
            model.buyingTimesProperty().add(newBuyingTime);
        }
    }

    private void handleModelListChanged(Change<? extends BuyingTime> event, TabPane tabPane, StockModel model) {
        while (event.next()) {
            for (BuyingTime newBuyingTime : event.getAddedSubList()) {
                BuyingTimeTab newBuyingTimeTab = new BuyingTimeTab(newBuyingTime, model);
                tabPane.getTabs().add(newBuyingTimeTab);
                tabPane.getSelectionModel().select(newBuyingTimeTab);
            }
        }
    }

}

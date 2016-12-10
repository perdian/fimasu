package de.perdian.personal.stockimporter.fx.panels;

import de.perdian.personal.stockimporter.model.StockModel;
import de.perdian.personal.stockimporter.model.TransactionGroup;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class StockModelPane extends BorderPane {

    public StockModelPane(StockModel model) {

        MenuItem createTransactionGroupItem = new MenuItem("Add transaction group");
        createTransactionGroupItem.setGraphic(new ImageView(new Image(StockModelPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        createTransactionGroupItem.setOnAction(event -> this.handleCreateButtonClicked(model, this.getScene().getWindow()));
        ContextMenu contextMenu = new ContextMenu(createTransactionGroupItem);

        TabPane tabPane = new TabPane();
        for (TransactionGroup transactionGroup : model.transactionGroupsProperty()) {
            tabPane.getTabs().add(new TransactionGroupTab(transactionGroup, model));
        }
        tabPane.setContextMenu(contextMenu);

        this.setCenter(tabPane);

        model.transactionGroupsProperty().addListener((ListChangeListener<TransactionGroup>)(event -> this.handleModelListChanged(event, tabPane, model)));

    }

    private void handleCreateButtonClicked(StockModel model, Window parentWindow) {
        TransactionGroup newTransactionGroup = TransactionGroupCreationDialog.createTransactionGroup(parentWindow);
        if (newTransactionGroup != null) {
            model.transactionGroupsProperty().add(newTransactionGroup);
        }
    }

    private void handleModelListChanged(Change<? extends TransactionGroup> event, TabPane tabPane, StockModel model) {
        while (event.next()) {
            for (TransactionGroup newTransactionGroup : event.getAddedSubList()) {
                TransactionGroupTab newTransactionGroupTab = new TransactionGroupTab(newTransactionGroup, model);
                tabPane.getTabs().add(newTransactionGroupTab);
                tabPane.getSelectionModel().select(newTransactionGroupTab);
            }
        }
    }

}

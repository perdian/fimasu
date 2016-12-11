package de.perdian.personal.stockqifgenerator.fx;

import de.perdian.personal.stockqifgenerator.model.StockQifGeneratorModel;
import de.perdian.personal.stockqifgenerator.model.TransactionGroup;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

public class StockQifGeneratorModelPane extends BorderPane {

    public StockQifGeneratorModelPane(StockQifGeneratorModel model) {

        MenuItem createTransactionGroupItem = new MenuItem("Add transaction group");
        createTransactionGroupItem.setGraphic(new ImageView(new Image(StockQifGeneratorModelPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
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

    private void handleCreateButtonClicked(StockQifGeneratorModel model, Window parentWindow) {
        TransactionGroup newTransactionGroup = TransactionGroupCreationDialog.createTransactionGroup(parentWindow);
        if (newTransactionGroup != null) {
            model.transactionGroupsProperty().add(newTransactionGroup);
        }
    }

    private void handleModelListChanged(Change<? extends TransactionGroup> event, TabPane tabPane, StockQifGeneratorModel model) {
        while (event.next()) {
            for (TransactionGroup newTransactionGroup : event.getAddedSubList()) {
                TransactionGroupTab newTransactionGroupTab = this.createTransactionGroupTab(newTransactionGroup, model);
                tabPane.getTabs().add(newTransactionGroupTab);
                tabPane.getSelectionModel().select(newTransactionGroupTab);
            }
        }
    }

    private TransactionGroupTab createTransactionGroupTab(TransactionGroup transactionGroup, StockQifGeneratorModel model) {
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

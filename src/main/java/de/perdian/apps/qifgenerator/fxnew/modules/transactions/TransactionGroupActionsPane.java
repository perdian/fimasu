package de.perdian.apps.qifgenerator.fxnew.modules.transactions;

import de.perdian.apps.qifgenerator.fxnew.model.Transaction;
import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import de.perdian.apps.qifgenerator.fxnew.support.components.ComponentBuilder;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

class TransactionGroupActionsPane extends VBox {

    TransactionGroupActionsPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder) {

        Button addButton = new Button("Add transaction");
        addButton.setGraphic(new ImageView(new Image(TransactionGroupPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        addButton.setOnAction(event -> transactionGroup.transactionsProperty().add(new Transaction()));
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setFocusTraversable(false);
        Button exportButton = new Button("Export");
        exportButton.setGraphic(new ImageView(new Image(TransactionGroupPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        exportButton.setOnAction(new TransactionGroupExportEventHandler(transactionGroup));
        exportButton.setMaxWidth(Double.MAX_VALUE);
        exportButton.setFocusTraversable(false);

        this.getChildren().addAll(addButton, exportButton);
        this.setSpacing(4);
        this.setPadding(new Insets(8, 4, 8, 4));

    }

}

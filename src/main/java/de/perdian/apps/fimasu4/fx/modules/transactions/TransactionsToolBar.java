package de.perdian.apps.fimasu4.fx.modules.transactions;

import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import de.perdian.apps.fimasu4.model.types.TransactionType;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class TransactionsToolBar extends ToolBar {

    TransactionsToolBar(ObjectProperty<TransactionGroup> selectedTransactionGroup) {

        ComponentFactory componentFactory = new ComponentFactory();

        Button addBuySellTransactionButton = componentFactory.createButton("Buy/Sell", MaterialDesignP.PLUS, new AddTransactionEventHandler(selectedTransactionGroup, TransactionType.BUY));
        addBuySellTransactionButton.disableProperty().bind(selectedTransactionGroup.isNull());
        Button addPayoutTransactionButton = componentFactory.createButton("Payout", MaterialDesignP.PLUS, new AddTransactionEventHandler(selectedTransactionGroup, TransactionType.PAYOUT));
        addPayoutTransactionButton.disableProperty().bind(selectedTransactionGroup.isNull());
        this.getItems().addAll(addBuySellTransactionButton, addPayoutTransactionButton);

        HBox separatorBox = new HBox();
        HBox.setHgrow(separatorBox, Priority.ALWAYS);
        this.getItems().add(separatorBox);

        Button importFromFilesButton = componentFactory.createButton("Import from files", MaterialDesignA.APPLICATION_IMPORT, event -> {

        });
        Button exportAsQifButton = componentFactory.createButton("Export as QIF", MaterialDesignA.APPLICATION_EXPORT, event -> {

        });
        this.getItems().addAll(importFromFilesButton, exportAsQifButton);

    }

}

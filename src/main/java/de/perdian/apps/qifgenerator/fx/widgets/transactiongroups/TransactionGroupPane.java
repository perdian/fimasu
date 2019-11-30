package de.perdian.apps.qifgenerator.fx.widgets.transactiongroups;

import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.widgets.transactions.TransactionsPane;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class TransactionGroupPane extends VBox {

    TransactionGroupPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder, Preferences preferences) {

        TransactionsPane transactionsPane = new TransactionsPane(transactionGroup.getTransactions(), componentBuilder, preferences);
        TitledPane transactionsTitledPane = new TitledPane("Transactions", transactionsPane);
        transactionsTitledPane.setCollapsible(false);
        transactionsTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(transactionsTitledPane, Priority.ALWAYS);

        TransactionGroupExportPane actionsPane = new TransactionGroupExportPane(transactionGroup, componentBuilder, preferences);
        TitledPane actionsTitledPane = new TitledPane("Export", actionsPane);
        actionsTitledPane.setCollapsible(false);

        this.setSpacing(8);
        this.setPadding(new Insets(8, 8, 8, 8));
        this.getChildren().addAll(transactionsTitledPane, actionsTitledPane);

    }

}

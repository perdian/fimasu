package de.perdian.apps.qifgenerator.fx.modules.transactions;

import de.perdian.apps.qifgenerator.fx.QifGeneratorHelper;
import de.perdian.apps.qifgenerator.fx.model.TransactionGroup;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class TransactionGroupPane extends GridPane {

    public TransactionGroupPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder) {

        TransactionGroupItemsPane itemsPane = new TransactionGroupItemsPane(transactionGroup.transactionsProperty(), componentBuilder);
        TitledPane itemsTitledPane = new TitledPane("Transactions", itemsPane);
        itemsTitledPane.setMaxHeight(Double.MAX_VALUE);
        itemsTitledPane.setCollapsible(false);
        GridPane.setHgrow(itemsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(itemsTitledPane, Priority.ALWAYS);

        TransactionGroupDetailsPane detailsPane = new TransactionGroupDetailsPane(transactionGroup, componentBuilder);
        TitledPane detailsTitledPane = QifGeneratorHelper.wrapInTitledPane("Details", detailsPane);
        GridPane.setHgrow(detailsTitledPane, Priority.ALWAYS);

        TransactionGroupActionsPane actionsPane = new TransactionGroupActionsPane(transactionGroup, componentBuilder);
        TitledPane actionsTitledPane = QifGeneratorHelper.wrapInTitledPane("Actions", actionsPane);

        this.add(itemsTitledPane, 0, 0, 2, 1);
        this.add(detailsTitledPane, 0, 1, 1, 1);
        this.add(actionsTitledPane, 1, 1, 1, 1);
        this.setHgap(4);
        this.setVgap(4);
        this.setPadding(new Insets(4, 0, 0, 0));

    }

}

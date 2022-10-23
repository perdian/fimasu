package de.perdian.apps.fimasu4.fx;

import de.perdian.apps.fimasu4.fx.modules.files.FilesListPane;
import de.perdian.apps.fimasu4.fx.modules.transactiongroups.TransactionGroupsPane;
import de.perdian.apps.fimasu4.fx.modules.transactions.TransactionsPane;
import de.perdian.apps.fimasu4.model.FimasuModel;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * The main pane under which the whole FiMaSu application is being built
 *
 * @author Christian Seifert
 */

class FimasuApplicationPane extends GridPane {

    FimasuApplicationPane(FimasuModel model, FimasuPreferences preferences) {

        TransactionGroupsPane transactionGroupsPane = new TransactionGroupsPane(model);
        TitledPane transactionGroupsTitledPane = new TitledPane("Transaction groups", transactionGroupsPane);
        transactionGroupsTitledPane.setFocusTraversable(false);
        transactionGroupsTitledPane.setCollapsible(false);
        transactionGroupsTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(transactionGroupsTitledPane, Priority.ALWAYS);

        FilesListPane filesListPane = new FilesListPane(preferences);
        TitledPane filesListTitledPane = new TitledPane("Files", filesListPane);
        filesListTitledPane.setFocusTraversable(false);
        filesListTitledPane.setCollapsible(false);
        filesListTitledPane.setPrefWidth(400);
        filesListTitledPane.setPrefHeight(0);

        TransactionsPane transactionsPane = new TransactionsPane(model.getSelectedTransactionGroup(), filesListPane.getFiles());
        TitledPane transactionsTitledPane = new TitledPane("Transactions", transactionsPane);
        transactionsTitledPane.setFocusTraversable(false);
        transactionsTitledPane.setCollapsible(false);
        transactionsTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(transactionsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(transactionsTitledPane, Priority.ALWAYS);

        this.add(transactionGroupsTitledPane, 0, 0, 1, 1);
        this.add(filesListTitledPane, 1, 0, 1, 1);
        this.add(transactionsTitledPane, 0, 2, 2, 1);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(5);

    }

}

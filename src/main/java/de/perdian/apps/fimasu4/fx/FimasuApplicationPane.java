package de.perdian.apps.fimasu4.fx;

import de.perdian.apps.fimasu4.fx.modules.files.FilesContentPane;
import de.perdian.apps.fimasu4.fx.modules.files.FilesListPane;
import de.perdian.apps.fimasu4.fx.modules.transactiongroups.TransactionGroupsPane;
import de.perdian.apps.fimasu4.fx.modules.transactions.TransactionsPane;
import de.perdian.apps.fimasu4.model.TransactionGroupModel;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The main pane under which the whole FiMaSu application is being built
 *
 * @author Christian Seifert
 */

class FimasuApplicationPane extends GridPane {

    FimasuApplicationPane(TransactionGroupModel transactionGroupModel) {

        TransactionGroupsPane transactionGroupsPane = new TransactionGroupsPane(transactionGroupModel);
        TitledPane transactionGroupsTitledPane = new TitledPane("Transaction groups", transactionGroupsPane);
        transactionGroupsTitledPane.setFocusTraversable(false);
        transactionGroupsTitledPane.setCollapsible(false);
        transactionGroupsTitledPane.setMaxHeight(Double.MAX_VALUE);

        TransactionsPane transactionsPane = new TransactionsPane(transactionGroupModel.getSelectedTransactionGroup());
        TitledPane transactionsTitledPane = new TitledPane("Transactions", transactionsPane);
        transactionsTitledPane.setFocusTraversable(false);
        transactionsTitledPane.setCollapsible(false);
        transactionsTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(transactionsTitledPane, Priority.ALWAYS);

        FilesListPane filesListPane = new FilesListPane();
        TitledPane filesListTitledPane = new TitledPane("Files", filesListPane);
        filesListTitledPane.setFocusTraversable(false);
        filesListTitledPane.setCollapsible(false);
        filesListTitledPane.setMaxHeight(Double.MAX_VALUE);

        FilesContentPane filesContentPane = new FilesContentPane();
        TitledPane filesContentTitledPane = new TitledPane("Content", filesContentPane);
        filesContentTitledPane.setFocusTraversable(false);
        filesContentTitledPane.setCollapsible(false);
        filesContentTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(filesContentTitledPane, Priority.ALWAYS);

        VBox leftBox = new VBox(10);
        leftBox.getChildren().addAll(transactionGroupsTitledPane, transactionsTitledPane);
        GridPane.setHgrow(leftBox, Priority.ALWAYS);
        GridPane.setVgrow(leftBox, Priority.ALWAYS);

        VBox rightBox = new VBox(10);
        rightBox.getChildren().addAll(filesListTitledPane, filesContentTitledPane);
        rightBox.setMinWidth(400);
        GridPane.setVgrow(rightBox, Priority.ALWAYS);

        this.add(leftBox, 0, 0, 1, 1);
        this.add(rightBox, 1, 0, 1, 1);

        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(10);

    }

}

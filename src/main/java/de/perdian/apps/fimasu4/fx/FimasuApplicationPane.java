package de.perdian.apps.fimasu4.fx;

import de.perdian.apps.fimasu4.fx.modules.files.FilesContentPane;
import de.perdian.apps.fimasu4.fx.modules.files.FilesListPane;
import de.perdian.apps.fimasu4.fx.modules.transactiongroups.TransactionGroupsPane;
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

    FimasuApplicationPane() {

        TransactionGroupsPane transactionGroupsPane = new TransactionGroupsPane();
        TitledPane transactionGroupsTitledPane = new TitledPane("Transaction groups", transactionGroupsPane);
        transactionGroupsTitledPane.setCollapsible(false);
        transactionGroupsTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(transactionGroupsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(transactionGroupsTitledPane, Priority.ALWAYS);

        FilesListPane filesListPane = new FilesListPane();
        TitledPane filesListTitledPane = new TitledPane("Files", filesListPane);
        filesListTitledPane.setCollapsible(false);
        filesListTitledPane.setMinWidth(300);
        filesListTitledPane.setMaxHeight(Double.MAX_VALUE);

        FilesContentPane filesContentPane = new FilesContentPane();
        TitledPane filesContentTitledPane = new TitledPane("Content", filesContentPane);
        filesContentTitledPane.setCollapsible(false);
        filesContentTitledPane.setMinWidth(300);
        filesContentTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(filesContentTitledPane, Priority.ALWAYS);

        this.add(transactionGroupsTitledPane, 0, 0, 1, 2);
        this.add(filesListTitledPane, 1, 0, 1, 1);
        this.add(filesContentTitledPane, 1, 1, 1, 1);

        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(10);
        this.setVgap(10);

    }

}

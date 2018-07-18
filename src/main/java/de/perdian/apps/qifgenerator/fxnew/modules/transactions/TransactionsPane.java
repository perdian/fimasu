package de.perdian.apps.qifgenerator.fxnew.modules.transactions;

import de.perdian.apps.qifgenerator.fxnew.QifGeneratorHelper;
import de.perdian.apps.qifgenerator.fxnew.QifGeneratorPreferences;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;

public class TransactionsPane extends BorderPane {

    public TransactionsPane(QifGeneratorPreferences preferences) {

        TabPane tabPane = new TabPane();

        TitledPane titledPane = QifGeneratorHelper.wrapInTitledPane("Transactions", tabPane);
        titledPane.setMaxWidth(Double.MAX_VALUE);
        titledPane.setMaxHeight(Double.MAX_VALUE);

        this.setCenter(titledPane);

    }

}
